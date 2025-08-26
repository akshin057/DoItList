package com.example.doitlist.presentation.projects

import android.R.attr.thickness
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.doitlist.R
import com.example.doitlist.domain.model.Project
import com.example.doitlist.presentation.tasks.TasksViewModel
import com.example.doitlist.presentation.ui.BottomNavBar
import com.example.doitlist.presentation.ui.LocalDrawerActions
import com.example.doitlist.presentation.ui.MenuAction
import com.example.doitlist.presentation.ui.NeonFab
import com.example.doitlist.presentation.ui.ProjectSwipeItem
import com.example.doitlist.presentation.ui.ProjectBottomSheet
import com.example.doitlist.presentation.ui.Screen
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.presentation.ui.theme.FabBackColor
import com.example.doitlist.presentation.ui.theme.TextColor
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(navController: NavController, vm: ProjectViewModel, taskVm: TasksViewModel) {

    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by vm.uiState.collectAsState()

    var editingProject by remember { mutableStateOf<Project?>(null) }

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
        containerColor = BackColor,
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

                IconButton(onClick = { vm.onDeleteAllProjects() }) {
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
        floatingActionButton = {
            NeonFab(onClick = {
                editingProject = null
                scope.launch { sheetState.show() }
            })
        },
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MenuAction(
                    R.drawable.routine,
                    "Рутина"
                ) { navController.navigate(Screen.RoutineScreen.route) }
                MenuAction(R.drawable.statistics, "Статистика") { /* nav */ }
                MenuAction(R.drawable.search, "Поиск") { /* nav */ }
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(uiState.projects) { project ->
                            ProjectSwipeItem(
                                project,
                                onClick = {
                                    editingProject = project
                                    scope.launch { sheetState.show() }
                                },
                                onDelete = {
                                    vm.onDeleteProject(it)
                                }
                            )
                        }
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
                ProjectBottomSheet(
                    onDismiss = { scope.launch { sheetState.hide() } },
                    onSave = { project ->
                        scope.launch {
                            if (editingProject == null) {
                                val today = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date
                                vm.onCreateProject(
                                    name = project.name,
                                    description = project.description,
                                    startDate = today,
                                    endDate = project.endDate,
                                )
                            } else {
                                vm.onUpdateProject(project)
                            }
                            editingProject = null
                            sheetState.hide()
                        }
                    },
                    project = editingProject,
                    navController = navController
                )
            }
        }
    }
}