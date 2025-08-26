package com.example.doitlist.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doitlist.domain.model.Task
import com.example.doitlist.domain.usecases.TaskManager
import com.example.doitlist.utils.OverdueSection
import com.example.doitlist.utils.ProjectUiState
import com.example.doitlist.utils.TasksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import javax.inject.Inject
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskManager: TaskManager
) : ViewModel() {

    private val _progress = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    private val tz = TimeZone.currentSystemDefault()
    private val selectedDate = MutableStateFlow(System.now().toLocalDateTime(tz).date)

    val overdueSections: StateFlow<List<OverdueSection>> =
        taskManager.observeTasks()
            .map { tasks ->
                val now = System.now()
                val grouped = tasks
                    .asSequence()
                    .filter { it.isOverdueInstant(now) }
                    .groupBy { it.endDate!!.toLocalDateTime(tz).date }
                grouped.entries
                    .sortedByDescending { it.key }
                    .map { (date, list) ->
                        OverdueSection(
                            date = date,
                            tasks = list.sortedBy { it.endDate }
                        )
                    }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val uiState: StateFlow<TasksUiState> =
        combine(
            taskManager.observeTasks(),
            selectedDate,
            _progress,
            _error
        ) { tasks, date, progress, error ->
            val filtered = tasks.filter { it.occursOn(date, tz) }
            TasksUiState(
                tasks = filtered,
                isLoading = progress,
                error = error
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TasksUiState(isLoading = true)
            )

    init {
        onRefresh()
    }

    fun onRefresh() = safeRun { taskManager.refresh() }

    fun onCreateTask(
        name: String, description: String?, projectId: Long?,
        recurrenceId: Long?, importance: Int, startDate: Instant?,
        endDate: Instant?, completedAt: Instant?, reminderTime: LocalTime?, updatedAt: Instant
    ) = safeRun {
        val actualStart = startDate ?: System.now()
        taskManager.create(
            Task(
                remoteId = null,
                localId = null,
                name = name,
                description = description,
                projectId = projectId,
                recurrenceId = recurrenceId,
                isDone = false,
                importance = importance,
                startDate = actualStart,
                endDate = endDate,
                completedAt = completedAt,
                reminderTime = reminderTime,
                updatedAt = updatedAt
            )
        )
    }

    fun onCompleteTask(task: Task) = safeRun {
        taskManager.complete(task)
    }

    fun onUpdateTask(task: Task) = safeRun {
        taskManager.update(task)
    }

    fun onDeleteTask(task: Task) = safeRun {
        task.localId ?: error("Задача еще не синхронизрована")
        taskManager.delete(task.localId)
    }

    fun onDeleteAllTasks() = safeRun {
        taskManager.deleteAllTasks()
    }


    fun onRescheduleTask(task: Task) = safeRun {
        taskManager.rescheduleTask(task)
    }

    fun onDeleteProjectTasks(projectId: Long) = safeRun {
        taskManager.deleteTasksByProject(projectId)
    }

    fun onRescheduleTasks(tasks: List<Task>) = safeRun {
        tasks.forEach { taskManager.rescheduleTask(it) }
    }

    fun observeProjectTasks(projectId: Long): StateFlow<List<Task>> =
        taskManager.observeTasksByProject(projectId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private inline fun safeRun(crossinline block: suspend () -> Unit) =
        viewModelScope.launch {
            _progress.value = true
            _error.value = null
            try {
                block()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _progress.value = false
            }
        }

    private fun Task.occursOn(date: LocalDate, tz: TimeZone): Boolean {
        val start = startDate.toLocalDateTime(tz).date
        val end = (endDate ?: startDate).toLocalDateTime(tz).date
        return date >= start && date <= end
    }

    private fun Task.isOverdueInstant(now: Instant): Boolean =
        !isDone && endDate?.let { it < now } == true
}
