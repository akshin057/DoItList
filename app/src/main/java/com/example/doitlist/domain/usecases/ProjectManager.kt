package com.example.doitlist.domain.usecases

import com.example.doitlist.domain.model.Project
import com.example.doitlist.domain.model.Task
import com.example.doitlist.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import javax.inject.Inject

class ProjectManager @Inject constructor(
    private val repo: ProjectRepository
) {

    suspend fun create(project: Project) = repo.createProject(project)

    suspend fun update(project: Project) = repo.updateProject(project)

    suspend fun delete(localId: Long) = repo.deleteProject(localId)

    suspend fun deleteAll() = repo.deleteAllProjects()

    fun observeProjects(): Flow<List<Project>> = repo.observeProjects()

    suspend fun refresh() = repo.refreshProjects()

    suspend fun getProjectsByDate(date: Instant) : List<Project> = repo.getProjectsByDate(date)
}