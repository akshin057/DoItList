package com.example.doitlist.domain.repository

import com.example.doitlist.domain.model.Project
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface ProjectRepository {

    fun observeProjects(): Flow<List<Project>>

    suspend fun createProject(project: Project)

    suspend fun updateProject(project: Project)

    suspend fun deleteProject(id: Long)

    suspend fun deleteAllProjects()

    suspend fun refreshProjects()

    suspend fun getProjectsByDate(date: Instant): List<Project>
}