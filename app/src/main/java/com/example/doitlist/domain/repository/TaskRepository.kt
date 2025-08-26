package com.example.doitlist.domain.repository

import com.example.doitlist.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TaskRepository {

    fun observeTasks(): Flow<List<Task>>

    suspend fun createTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(id: Long)

    suspend fun deleteAllTasks()

    suspend fun complete(task: Task)

    suspend fun refreshTasks()

    suspend fun getTasksByDate(date: Instant): List<Task>

    fun observeTasksByProject(projectId: Long): Flow<List<Task>>

    suspend fun deleteTasksByProject(projectId: Long)

    suspend fun rescheduleTask(task: Task)
}