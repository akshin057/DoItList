package com.example.doitlist.presentation.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.doitlist.presentation.ui.BottomNavBar
import com.example.doitlist.presentation.ui.theme.BackColor
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.example.doitlist.R
import com.example.doitlist.domain.model.Task
import com.example.doitlist.presentation.projects.ProjectViewModel
import com.example.doitlist.presentation.ui.NeonFab
import com.example.doitlist.presentation.ui.TaskBottomSheet
import com.example.doitlist.presentation.ui.TaskSwipeItem
import com.example.doitlist.presentation.ui.theme.FabBackColor
import com.example.doitlist.presentation.ui.theme.TextColor
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(navController: NavController, vm: TasksViewModel, projectVm: ProjectViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }

    var editingTask by remember { mutableStateOf<Task?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    val uiState by vm.uiState.collectAsState()

    val isRefreshing = uiState.isRefreshing
    val pullState = rememberPullToRefreshState()

    val projectUiState by projectVm.uiState.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            println(message)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = { BottomNavBar(navController) },
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                }

                IconButton(onClick = { vm.onDeleteAllTasks() }) {
                    Icon(
                        painter = painterResource(R.drawable.bin),
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = BackColor,
        floatingActionButton = {
            NeonFab(onClick = {
                editingTask = null
                scope.launch { sheetState.show() }
            })
        }
    ) { padding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            PullToRefreshBox(
                state = pullState,
                isRefreshing = isRefreshing,
                onRefresh = { vm.onRefresh() },
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        state = pullState,
                        isRefreshing = isRefreshing,
                        containerColor = FabBackColor,
                        color = TextColor,
                    )
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(uiState.tasks) { task ->
                        TaskSwipeItem(
                            task,
                            onDelete = { vm.onDeleteTask(it) },
                            onComplete = { vm.onCompleteTask(it) },
                            onClick = {
                                editingTask = task
                                scope.launch { sheetState.show() }
                            }
                        )
                    }
                }
            }
        }

        if (sheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = { scope.launch { sheetState.hide() } },
                sheetState = sheetState,
                tonalElevation = 8.dp,
                containerColor = BackColor
            ) {
                TaskBottomSheet(
                    onDismiss = { scope.launch { sheetState.hide() } },
                    onSave = { task ->
                        scope.launch {
                            if (editingTask == null) {
                                val now = System.now()
                                vm.onCreateTask(
                                    name = task.name,
                                    description = task.description,
                                    projectId = task.projectId,
                                    recurrenceId = task.recurrenceId,
                                    importance = task.importance,
                                    startDate = now,
                                    endDate = task.endDate,
                                    completedAt = null,
                                    reminderTime = null,
                                    updatedAt = now
                                )
                            } else {
                                vm.onUpdateTask(task)
                            }
                            editingTask = null
                            sheetState.hide()
                        }
                    },
                    task = editingTask,
                    projects = projectUiState.projects
                )
            }
        }
    }
}


