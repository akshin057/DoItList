package com.example.doitlist.domain.usecases

import com.example.doitlist.domain.model.Task
import com.example.doitlist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import javax.inject.Inject

class TaskManager @Inject constructor(
    private val repo: TaskRepository
) {
    fun observeTasks(): Flow<List<Task>> = repo.observeTasks()

    suspend fun create(task: Task) = repo.createTask(task)

    suspend fun update(task: Task) = repo.updateTask(task)

    suspend fun delete(localId: Long) = repo.deleteTask(localId)

    suspend fun deleteAllTasks() = repo.deleteAllTasks()

    suspend fun complete(task: Task) = repo.complete(task)

    suspend fun refresh() = repo.refreshTasks()

    suspend fun getTasksByDate(date: Instant): List<Task> = repo.getTasksByDate(date)

}