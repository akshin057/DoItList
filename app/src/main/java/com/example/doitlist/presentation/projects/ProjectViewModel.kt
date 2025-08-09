package com.example.doitlist.presentation.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doitlist.domain.model.Project
import com.example.doitlist.domain.usecases.ProjectManager
import com.example.doitlist.utils.ProjectUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Instant

@HiltViewModel
class ProjectViewModel @Inject constructor(
    private val projectManager: ProjectManager
) : ViewModel() {

    private val _progress = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProjectUiState> =
        combine(
            projectManager.observeProjects(),
            _progress,
            _error
        ) { projects, progress, error ->
            ProjectUiState(
                projects = projects,
                isLoading = progress,
                error = error
            )
        }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProjectUiState(isLoading = true)
            )

    init {
        onRefresh()
    }

    fun onRefresh() = safeRun {
        projectManager.refresh()
    }

    fun onCreateProject(
        name: String,
        description: String?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ) = safeRun {
        projectManager.create(
            Project(
                remoteId = null,
                localId = null,
                name = name,
                description = description,
                startDate = startDate ?: Clock.System.todayIn(TimeZone.currentSystemDefault()),
                endDate = endDate
            )
        )
    }

    fun onUpdateProject(project: Project) = safeRun { projectManager.update(project) }

    fun onDeleteProject(project: Project) = safeRun {
        project.localId ?: error("Проект ещё не сохранён локально")
        projectManager.delete(project.localId)
    }

    fun onDeleteAllProjects() = safeRun { projectManager.deleteAll() }

    suspend fun getProjectsByDate(date: Instant) : List<Project> =
        projectManager.getProjectsByDate(date)

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