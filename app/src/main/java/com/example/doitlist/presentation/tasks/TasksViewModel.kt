package com.example.doitlist.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doitlist.domain.model.Task
import com.example.doitlist.domain.usecases.TaskManager
import com.example.doitlist.utils.ProjectUiState
import com.example.doitlist.utils.TasksUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskManager: TaskManager
) : ViewModel() {

    private val _progress = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TasksUiState> =
        combine(
            taskManager.observeTasks(),
            _progress,
            _error
        ) { tasks, progress, error ->
            TasksUiState(
                tasks = tasks,
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

    suspend fun getTasksByDate(endDate: Instant): List<Task> =
        taskManager.getTasksByDate(date = endDate)

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
}
