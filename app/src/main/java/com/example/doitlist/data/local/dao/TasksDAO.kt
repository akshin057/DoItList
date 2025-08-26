package com.example.doitlist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.doitlist.data.local.models.ProjectEntity
import com.example.doitlist.data.local.models.TaskEntity
import com.example.doitlist.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import java.sql.Date

@Dao
interface TasksDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(taskEntity: TaskEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTask(taskEntity: TaskEntity)

    @Query("DELETE FROM tasks WHERE localId = :id")
    suspend fun deleteByLocalId(id: Long)

    @Query("SELECT * FROM tasks WHERE isDone = 0")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE localId = :localId LIMIT 1")
    suspend fun getByLocalId(localId: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Long): TaskEntity?

    @Upsert
    suspend fun upsertAll(tasks: List<TaskEntity>)

    @Upsert
    suspend fun upsert(tasks: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun clearAll()

    @Query("SELECT * FROM tasks WHERE isSynced = 0 AND remoteId IS NULL")
    suspend fun getUnsynchedTasks(): List<TaskEntity>

    @Query("UPDATE tasks SET isDone = 1 WHERE localId = :localId ")
    suspend fun completeTask(localId: Long)

    @Query("SELECT * FROM tasks WHERE isDone = 0")
    fun observeActiveTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks")
    suspend fun debugDump(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE endDate = :endDate")
    suspend fun getTasksByDate(endDate: Instant): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE projectId = :projectId AND isDone = 0")
    fun observeTasksByProject(projectId: Long): Flow<List<TaskEntity>>

    @Query("DELETE FROM tasks WHERE projectId = :projectId")
    suspend fun deleteTasksByProject(projectId: Long)

    @Query("UPDATE tasks SET endDate = :newEndDate, updatedAt = :updatedAt WHERE localId = :taskId")
    suspend fun rescheduleTask(taskId: Long, newEndDate: Instant, updatedAt: Instant)
}