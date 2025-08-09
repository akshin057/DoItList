package com.example.doitlist.data.remote.project

import com.example.doitlist.data.remote.dto.ProjectDTO

interface ProjectService {

    suspend fun createProject(projectDTO: ProjectDTO): Long

    suspend fun updateProject(id: Long, projectDTO: ProjectDTO): Boolean

    suspend fun getProjects(): List<ProjectDTO>

    suspend fun deleteProjects(id: Long): Boolean
}