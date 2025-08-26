package com.example.doitlist.presentation.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doitlist.R
import com.example.doitlist.domain.model.Task
import com.example.doitlist.presentation.tasks.TasksViewModel
import com.example.doitlist.presentation.ui.LocalDrawerActions
import com.example.doitlist.presentation.ui.NeonFab
import com.example.doitlist.presentation.ui.RoutineSwipeItem
import com.example.doitlist.presentation.ui.Screen
import com.example.doitlist.presentation.ui.TaskBottomSheet
import com.example.doitlist.presentation.ui.TaskSwipeItem
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.presentation.ui.theme.FabBackColor
import com.example.doitlist.presentation.ui.theme.TextColor
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskForProjectScreen(
    navController: NavController,
    vm: TasksViewModel,
    projectVm: ProjectViewModel,
    projectId: Long
) {

    val tasks by remember(projectId) { vm.observeProjectTasks(projectId) }
        .collectAsState(initial = emptyList())

    val projectUiState by projectVm.uiState.collectAsState()

    val currentProject = remember(projectUiState.projects, projectId) {
        projectUiState.projects.firstOrNull { it.localId == projectId }
    }

    val uiState by vm.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    var editingTask by remember { mutableStateOf<Task?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val scope = rememberCoroutineScope()

    val isRefreshing = uiState.isRefreshing
    val pullState = rememberPullToRefreshState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            snackbarHostState.showSnackbar(message)
            println(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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

                IconButton(onClick = {
                    vm.onDeleteProjectTasks(projectId)
                }) {
                    Icon(
                        painter = painterResource(R.drawable.bin),
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Screen.ProjectScreen.route)
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp)
                    )
                }

            }
        },
        containerColor = BackColor,
        floatingActionButton = {
            NeonFab(
                onClick = {
                    editingTask = null
                    scope.launch {
                        sheetState.show()
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(currentProject?.name ?: "Проект", color = TextColor, fontSize = 24.sp)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(currentProject?.description ?: "", color = TextColor, fontSize = 24.sp)
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                thickness = 3.dp,
                color = TextColor
            )

            Spacer(Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
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
                    if (tasks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "По этому проекту пока нет задач",
                                color = TextColor,
                                fontSize = 18.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(tasks) { task ->
                                TaskSwipeItem(
                                    task,
                                    onClick = {
                                        editingTask = task
                                        scope.launch { sheetState.show() }
                                    },
                                    onDelete = { vm.onDeleteTask(it) },
                                    onComplete = { vm.onCompleteTask(it) }
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
                                        val now = Clock.System.now()
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
                            projects = projectUiState.projects,
                            currentProject = currentProject
                        )
                    }
                }
            }
        }
    }
}