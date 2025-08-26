package com.example.doitlist.data.remote.task

import com.example.doitlist.data.remote.dto.TaskDTO

interface TaskService {

    suspend fun createTask(taskDTO: TaskDTO): Long

    suspend fun updateTask(id: Long, taskDTO: TaskDTO) : Boolean

    suspend fun completeTask(id: Long) : Boolean

    suspend fun deleteTask(id: Long) : Boolean

    suspend fun getTasks(): List<TaskDTO>

    suspend fun deleteTasksByProject(projectId: Long): Boolean

    suspend fun rescheduleTask(taskId: Long): Boolean
}