package com.example.doitlist.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.doitlist.data.local.models.ProjectEntity
import com.example.doitlist.data.local.models.ProjectWithTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface ProjectsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addProject(projectEntity: ProjectEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProject(projectEntity: ProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(projects: List<ProjectEntity>)

    @Upsert
    suspend fun upsertAll(projects: List<ProjectEntity>)

    @Upsert
    suspend fun upsert(project: ProjectEntity): Long

    @Query("DELETE FROM projects")
    suspend fun clearAll()

    @Query("SELECT * FROM projects WHERE isSynced = 0 AND remoteId IS NULL")
    suspend fun getUnsynchedProjects(): List<ProjectEntity>

    @Query("DELETE FROM projects WHERE localId = :localId")
    suspend fun deleteProject(localId: Long)

    @Query("SELECT * FROM projects WHERE localId = :localId LIMIT 1")
    suspend fun getByLocalId(localId: Long): ProjectEntity?

    @Query("SELECT * FROM projects")
    suspend fun getAllProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getByRemoteId(remoteId: Long): ProjectEntity?

    @Transaction
    @Query("SELECT * FROM projects WHERE localId = :id")
    suspend fun getProjectWithTasks(id: Long): ProjectWithTasks?

    @Query("SELECT * FROM projects")
    fun observeProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects")
    suspend fun getProjects(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE endDate = :endDate")
    suspend fun getProjectsByDate(endDate: Instant): List<ProjectEntity>
}