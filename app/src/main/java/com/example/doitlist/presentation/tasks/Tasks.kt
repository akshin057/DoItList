package com.example.doitlist.presentation.tasks

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.doitlist.R
import com.example.doitlist.domain.model.Task
import com.example.doitlist.presentation.projects.ProjectViewModel
import com.example.doitlist.presentation.ui.LocalDrawerActions
import com.example.doitlist.presentation.ui.MenuAction
import com.example.doitlist.presentation.ui.NeonFab
import com.example.doitlist.presentation.ui.Screen
import com.example.doitlist.presentation.ui.TaskBottomSheet
import com.example.doitlist.presentation.ui.TaskSwipeItem
import com.example.doitlist.presentation.ui.theme.FabBackColor
import com.example.doitlist.presentation.ui.theme.TextColor
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System

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

    val isRefreshing = uiState.isLoading
    val pullState = rememberPullToRefreshState()

    val projectUiState by projectVm.uiState.collectAsState()

    val overdue by vm.overdueSections.collectAsState()

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
                val drawer = LocalDrawerActions.current
                IconButton(onClick = {
                    drawer.open()
                }) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Сегодня",
                    textAlign = TextAlign.Start,
                    color = TextColor,
                    fontSize = 48.sp,
                    modifier = Modifier
                        .width(250.dp)
                        .padding(start = 35.dp)
                        .offset(y = 10.dp)
                )

                if (overdue.isNotEmpty()) {
                    MenuAction(R.drawable.todo, "", width = 90.dp, height = 90.dp, size = 80.dp) {
                        navController.navigate(Screen.OverdueTasksScreen.route)
                    }
                } else {
                    MenuAction(R.drawable.check, "", width = 80.dp, height = 80.dp, size = 70.dp) {
                        navController.navigate(Screen.OverdueTasksScreen.route)
                    }
                }

            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                thickness = 3.dp,
                color = TextColor
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
}


