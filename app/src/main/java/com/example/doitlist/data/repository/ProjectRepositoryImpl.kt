package com.example.doitlist.data.repository

import android.content.Context
import android.util.Log
import com.example.doitlist.data.local.AppDatabase
import com.example.doitlist.data.local.models.ProjectEntity
import com.example.doitlist.data.remote.project.ProjectService
import com.example.doitlist.domain.model.Project
import com.example.doitlist.domain.repository.ProjectRepository
import com.example.doitlist.utils.NetworkUtils
import com.example.doitlist.utils.mappers.toDTO
import com.example.doitlist.utils.mappers.toDomain
import com.example.doitlist.utils.mappers.toEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val projectService: ProjectService,
    private val db: AppDatabase
) : ProjectRepository {

    override suspend fun refreshProjects() = withContext(Dispatchers.IO) {
        if (!NetworkUtils.isNetworkAvailable(context)) return@withContext
        syncProjects()
        val remote = projectService.getProjects().map { it.toEntity() }
        db.projectsDao().upsertAll(remote)
    }

    override fun observeProjects(): Flow<List<Project>> =
        db.projectsDao().observeProjects().map { list -> list.map { it.toDomain() } }

    override suspend fun getProjectByLocalId(localId: Long): Project? =
        db.projectsDao().getByLocalId(localId)?.toDomain()

    override suspend fun createProject(project: Project) = withContext(Dispatchers.IO) {
        val localId = db.projectsDao().addProject(
            project.toEntity(isSynced = false)
        )

        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val remoteId = projectService.createProject(project.toDTO())
                db.projectsDao().updateProject(
                    project.copy(
                        localId = localId,
                        remoteId = remoteId
                    ).toEntity(isSynced = true)
                )
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun updateProject(project: Project) = withContext(Dispatchers.IO) {
        db.projectsDao().updateProject(project.toEntity())

        if (NetworkUtils.isNetworkAvailable(context) && project.remoteId != null) {
            try {
                projectService.updateProject(project.remoteId, project.toDTO())
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun deleteProject(localId: Long) = withContext(Dispatchers.IO) {
        val entity = db.projectsDao().getByLocalId(localId)
            ?: throw IllegalArgumentException("Задача с localId=$localId не найдена")
        db.projectsDao().deleteProject(localId)

        if (NetworkUtils.isNetworkAvailable(context) && entity.remoteId != null) {
            try {
                projectService.deleteProjects(entity.remoteId)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    private suspend fun syncProjects() {
        db.projectsDao().getUnsynchedProjects().forEach { entity ->
            try {
                val newRemoteId = projectService.createProject(entity.toDTO())
                db.projectsDao().updateProject(entity.copy(remoteId = newRemoteId, isSynced = true))
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun getProjectsByDate(date: Instant): List<Project> =
        withContext(Dispatchers.IO) {
            db.projectsDao().getProjectsByDate(date).map { it.toDomain() }
        }

    override suspend fun deleteAllProjects() = withContext(Dispatchers.IO) {
        db.projectsDao().clearAll()
    }

}