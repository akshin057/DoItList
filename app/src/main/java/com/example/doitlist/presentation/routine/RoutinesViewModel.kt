package com.example.doitlist.presentation.routine


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.todayIn
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.ViewModel
import com.example.doitlist.data.local.DateStorage
import com.example.doitlist.domain.model.Routine
import com.example.doitlist.domain.model.RoutineLog
import com.example.doitlist.domain.usecases.RoutineLogManager
import com.example.doitlist.domain.usecases.RoutineManager
import com.example.doitlist.utils.RoutineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val routineManager: RoutineManager,
    private val routineLogManager: RoutineLogManager,
    private val dateStorage: DateStorage
) : ViewModel() {

    private val _progress = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<RoutineUiState> =
        combine(
            routineManager.observeRoutines(),
            routineLogManager.observeRoutineLogs(),
            _progress,
            _error
        ) { routines, routineLogs, progress, error ->
            RoutineUiState(
                routines = routines,
                routineLogs = routineLogs,
                error = error,
                isLoading = progress
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RoutineUiState(isLoading = true)
        )

    init {

        onRefresh()
    }

    fun onRefresh() = safeRun {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

        val lastRefreshDate = dateStorage.getLastRefreshDate()

        if (lastRefreshDate != today) {
            routineManager.unCompleteAllRoutines()
            dateStorage.saveLastRefreshDate(today)
        }

        routineManager.refresh()
        routineLogManager.refresh()
    }

    fun onCreateRoutine(
        name: String,
        description: String?,
        startDate: LocalDate?,
        reminderTime: LocalTime?
    ) = safeRun {
        routineManager.create(
            Routine(
                remoteId = null,
                localId = null,
                name = name,
                description = description,
                startDate = startDate ?: Clock.System.todayIn(TimeZone.currentSystemDefault()),
                reminderTime = reminderTime
            )
        )
    }

    fun onCompleteRoutine(routine: Routine) = safeRun {
        routine.localId ?: error("Данная рутина еще не сохранена полностью")

        val routineLog = routineLogManager.getTodayRoutineLog(
            routineId = routine.localId,
            routineRemoteId = routine.remoteId
        )

        if (routineLog != null) return@safeRun

        routineLogManager.create(
            RoutineLog(
                remoteId = null,
                localId = null,
                routineId = routine.localId,
                doneAt = Clock.System.todayIn(TimeZone.currentSystemDefault())
            )
        )

        routineManager.complete(routine)
    }

    fun unCompleteRoutine(routine: Routine) = safeRun {
        routine.localId ?: error("Данная рутина еще не сохранена полностью")

        val routineLog = routineLogManager.getTodayRoutineLog(
            routineId = routine.localId,
            routineRemoteId = routine.remoteId
        )

        if (routineLog != null && routineLog.localId != null) {
            routineLogManager.delete(routineLog.localId)
            routineManager.unComplete(routine)
        }
    }

    fun onUpdateRoutine(routine: Routine) = safeRun {
        routineManager.update(routine = routine)
    }

    fun onDeleteRoutine(routine: Routine) = safeRun {
        routine.localId ?: error("Рутина еще не сохранена локально")
        routineManager.delete(routine.localId)
    }

    fun onDeleteAllRoutines() = safeRun { routineManager.deleteAll() }

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