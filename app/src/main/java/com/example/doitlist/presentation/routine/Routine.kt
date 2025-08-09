package com.example.doitlist.presentation.routine

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doitlist.R
import com.example.doitlist.domain.model.Project
import com.example.doitlist.domain.model.Routine
import com.example.doitlist.presentation.ui.BottomNavBar
import com.example.doitlist.presentation.ui.NeonFab
import com.example.doitlist.presentation.ui.ProjectBottomSheet
import com.example.doitlist.presentation.ui.ProjectSwipeItem
import com.example.doitlist.presentation.ui.RoutineBottomSheet
import com.example.doitlist.presentation.ui.RoutineSwipeItem
import com.example.doitlist.presentation.ui.TaskSwipeItem
import com.example.doitlist.presentation.ui.textStyle
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
fun RoutineScreen(navController: NavController, vm: RoutinesViewModel) {

    val snackbarHostState = remember { SnackbarHostState() }

    val uiState by vm.uiState.collectAsState()

    var editingRoutine by remember { mutableStateOf<Routine?>(null) }

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
                IconButton(onClick = {}) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        tint = TextColor,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                    )
                }

                IconButton(onClick = { vm.onDeleteAllRoutines() }) {
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
                editingRoutine = null
                scope.launch { sheetState.show() }
            })
        },
        bottomBar = { BottomNavBar(navController) }

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(R.drawable.routine),
                contentDescription = null,
                modifier = Modifier.size(140.dp)
            )

            Text(
                "Выполняй задачи каждый день и заводи новые привычки",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(250.dp)
            )

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
                        items(uiState.routines) { routine ->
                            RoutineSwipeItem(
                                routine,
                                onClick = {
                                    editingRoutine = routine
                                    scope.launch { sheetState.show() }
                                },
                                onDelete = {
                                    vm.onDeleteRoutine(it)
                                },
                                onComplete = {
                                    vm.onCompleteRoutine(it)
                                },
                                unComplete = {
                                    vm.unCompleteRoutine(it)
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
                RoutineBottomSheet(
                    onDismiss = { scope.launch { sheetState.hide() } },
                    onSave = { routine ->
                        scope.launch {
                            if (editingRoutine == null) {
                                val today = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .date
                                vm.onCreateRoutine(
                                    name = routine.name,
                                    description = routine.description,
                                    startDate = today,
                                    reminderTime = routine.reminderTime
                                )
                            } else {
                                vm.onUpdateRoutine(routine)
                            }
                            editingRoutine = null
                            sheetState.hide()
                        }
                    },
                    routine = editingRoutine
                )
            }
        }
    }
}