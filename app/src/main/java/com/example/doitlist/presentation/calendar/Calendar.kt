package com.example.doitlist.presentation.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.doitlist.domain.model.Task
import com.example.doitlist.presentation.projects.ProjectViewModel
import com.example.doitlist.presentation.tasks.TasksViewModel
import com.example.doitlist.presentation.ui.BottomNavBar
import com.example.doitlist.presentation.ui.Calendar
import com.example.doitlist.presentation.ui.LocalDrawerActions
import com.example.doitlist.presentation.ui.NeonFab
import com.example.doitlist.presentation.ui.TaskBottomSheet
import com.example.doitlist.presentation.ui.TaskListItem
import com.example.doitlist.presentation.ui.TaskSwipeItem
import com.example.doitlist.presentation.ui.theme.BackColor
import com.example.doitlist.presentation.ui.theme.TextColor
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
//    CalendarScreen(
//        navController = rememberNavController(),
//        tasksViewModel = hiltViewModel()
//    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    vm: TasksViewModel,
    projectVm: ProjectViewModel
) {

    val uiState by vm.uiState.collectAsState()

    val tz = remember { TimeZone.currentSystemDefault() }
    val today = remember { kotlinx.datetime.Clock.System.now().toLocalDateTime(tz).date }

    var currentMonthFirst by remember {
        mutableStateOf(kotlinx.datetime.LocalDate(today.year, today.monthNumber, 1))
    }

    val year = currentMonthFirst.year
    val monthNumber = currentMonthFirst.monthNumber

    var selectedDay by remember(currentMonthFirst) {
        mutableStateOf(if (today.year == year && today.monthNumber == monthNumber) today.dayOfMonth else 1)
    }

    val daysInMonth = remember(currentMonthFirst) {
        val first = currentMonthFirst
        first.plus(kotlinx.datetime.DatePeriod(months = 1))
            .minus(kotlinx.datetime.DatePeriod(days = 1))
            .dayOfMonth
    }

    val offset =
        remember(currentMonthFirst) { currentMonthFirst.dayOfWeek.isoDayNumber - 1 } // Пн=0
    val weeks = remember(daysInMonth, offset) {
        val total = offset + daysInMonth
        kotlin.math.max(5, kotlin.math.ceil(total / 7.0).toInt())
    }

    val calendarInputData = remember(uiState.tasks, currentMonthFirst) {
        (1..daysInMonth).map { day ->
            val date = kotlinx.datetime.LocalDate(year, monthNumber, day)
            val tasksForDay = uiState.tasks.filter { t ->
                t.endDate?.toLocalDateTime(tz)?.date == date
            }
            CalendarInput(day = day, taskList = tasksForDay)
        }
    }

    val clickedCalendarInput = remember(selectedDay, calendarInputData) {
        calendarInputData.firstOrNull { it.day == selectedDay }
    }


    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val scope = rememberCoroutineScope()

    var editingTask by remember { mutableStateOf<Task?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    val projectUiState by projectVm.uiState.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        containerColor = BackColor,
        topBar = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.Start,
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
            }
        },
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            NeonFab(onClick = {
                scope.launch { sheetState.show() }
            })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    currentMonthFirst =
                        currentMonthFirst.minus(kotlinx.datetime.DatePeriod(months = 1))
                }) { Text("‹", fontSize = 28.sp, color = TextColor) }

                Text(
                    text = "${monthNameRu(monthNumber)} $year",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextColor
                )

                IconButton(onClick = {
                    currentMonthFirst =
                        currentMonthFirst.plus(kotlinx.datetime.DatePeriod(months = 1))
                }) { Text("›", fontSize = 28.sp, color = TextColor) }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс").forEach {
                    Text(it, fontSize = 18.sp, color = TextColor)
                }
            }

            Calendar(
                onDayClick = { day -> selectedDay = day },
                calendarInput = calendarInputData,
                weeks = weeks,
                firstWeekdayOffset = offset,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
                    .heightIn(max = 320.dp)
                    .padding(horizontal = 10.dp)
            )

            Text(
                text = "Задачи на $selectedDay ${monthNameRu(monthNumber).lowercase()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            val itemsList = clickedCalendarInput?.taskList.orEmpty()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                items(
                    items = itemsList,
                    key = { it.localId ?: it.remoteId ?: it.name }
                ) { task ->
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
                        date = kotlinx.datetime.LocalDate(year, monthNumber, selectedDay)
                    )
                }
            }
        }
    }
}

data class CalendarInput(
    val day: Int,
    val taskList: List<Task>
)

fun monthNameRu(month: Int): String = when (month) {
    1 -> "Январь"; 2 -> "Февраль"; 3 -> "Март"; 4 -> "Апрель"; 5 -> "Май"; 6 -> "Июнь"
    7 -> "Июль"; 8 -> "Август"; 9 -> "Сентябрь"; 10 -> "Октябрь"; 11 -> "Ноябрь"; else -> "Декабрь"
}