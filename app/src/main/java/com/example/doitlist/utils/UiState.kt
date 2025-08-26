package com.example.doitlist.utils

import com.example.doitlist.data.remote.dto.UserDTO
import com.example.doitlist.domain.model.Project
import com.example.doitlist.domain.model.Routine
import com.example.doitlist.domain.model.RoutineLog
import com.example.doitlist.domain.model.Task

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val token: String) : UiState
    data class Error(val throwable: Throwable) : UiState
}

sealed interface UserUiState {
    data object Idle : UserUiState
    data object Loading : UserUiState
    data class Success(val user: UserDTO) : UserUiState
    data class Error(val message: String?) : UserUiState
}

data class TasksUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)

data class ProjectUiState(
    val isLoading: Boolean = false,
    val projects: List<Project> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)

data class RoutineUiState(
    val isLoading: Boolean = false,
    val routines: List<Routine> = emptyList(),
    val routineLogs : List<RoutineLog> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)

data class RoutineLogUiState(
    val isLoading: Boolean = false,
    val routinesLogs: List<RoutineLog> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)