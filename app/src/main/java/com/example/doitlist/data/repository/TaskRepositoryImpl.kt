package com.example.doitlist.data.repository

import android.content.Context
import android.util.Log
import com.example.doitlist.data.local.AppDatabase
import com.example.doitlist.data.local.models.TaskEntity
import com.example.doitlist.data.remote.dto.TaskDTO
import com.example.doitlist.data.remote.project.ProjectService
import com.example.doitlist.data.remote.task.TaskService
import com.example.doitlist.domain.model.Task
import com.example.doitlist.domain.repository.TaskRepository
import com.example.doitlist.utils.NetworkUtils
import com.example.doitlist.utils.endOfToday
import com.example.doitlist.utils.mappers.toDTO
import com.example.doitlist.utils.mappers.toDomain
import com.example.doitlist.utils.mappers.toEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val taskService: TaskService,
    private val db: AppDatabase,
    private val projectService: ProjectService
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> =
        db.tasksDao().observeActiveTasks()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun refreshTasks() = withContext(Dispatchers.IO) {

        if (!NetworkUtils.isNetworkAvailable(context)) return@withContext

        syncTasks()

        val remoteProjects = projectService.getProjects().map { it.toEntity() }
        db.projectsDao().upsertAll(remoteProjects)

        val idMap = db.projectsDao()
            .getAllProjects()
            .associate { it.remoteId!! to it.localId!! }

        val remoteTasks = taskService.getTasks().map { dto ->
            dto.toEntity(localProjectId = dto.projectId?.let { idMap[it] })
        }

        db.tasksDao().upsertAll(remoteTasks)
    }

    override fun observeTasksByProject(projectId: Long): Flow<List<Task>> =
        db.tasksDao().observeTasksByProject(projectId)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun createTask(task: Task) = withContext(Dispatchers.IO) {

        val remoteProjId = task.projectId?.let { ensureProjectSynced(it) }

        val localId = db.tasksDao().addTask(
            task.copy(remoteId = null)
                .toEntity(isSynced = false)
        )

        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val remoteId = task.copy(projectId = task.projectId).toDTO(remoteProjId)
                    .let { dto -> taskService.createTask(dto) }

                db.tasksDao().updateTask(
                    task.copy(
                        localId = localId,
                        remoteId = remoteId
                    ).toEntity(isSynced = true)
                )
            } catch (e: Exception) {
                Log.e("TaskRepository", "createTask failed", e)
            }
        }
    }

    override suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {

        val remoteProjId = task.projectId?.let { ensureProjectSynced(it) }

        db.tasksDao().updateTask(task.toEntity())

        if (NetworkUtils.isNetworkAvailable(context) && task.remoteId != null) {
            try {
                taskService.updateTask(task.remoteId, task.toDTO(remoteProjId))
            } catch (e: Exception) {

            }
        }
    }

    override suspend fun deleteTask(localId: Long) = withContext(Dispatchers.IO) {

        val entity = db.tasksDao().getByLocalId(localId)
            ?: throw IllegalArgumentException("Задача с localId=$localId не найдена")
        db.tasksDao().deleteByLocalId(localId)

        if (NetworkUtils.isNetworkAvailable(context) && entity.remoteId != null) {
            try {
                taskService.deleteTask(entity.remoteId)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun rescheduleTask(task: Task) {
        val localId = task.localId ?: error("Невозможно перенести несохраненную задачу")

        db.tasksDao().rescheduleTask(localId, endOfToday(), Clock.System.now())

        val taskRemoteId = task.remoteId ?: db.tasksDao().getByLocalId(localId)?.remoteId

        if (NetworkUtils.isNetworkAvailable(context) && taskRemoteId != null) {
            try {
                taskService.rescheduleTask(taskRemoteId)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun deleteTasksByProject(projectId: Long) = withContext(Dispatchers.IO) {

        db.tasksDao().deleteTasksByProject(projectId)
        val projectRemoteId = db.projectsDao().getByLocalId(projectId)?.remoteId

        if (projectRemoteId != null && NetworkUtils.isNetworkAvailable(context)) {
            try {
                taskService.deleteTasksByProject(projectRemoteId)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun complete(task: Task) = withContext(Dispatchers.IO) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            if (task.remoteId == null) {
                syncTasks()
            }
            taskService.completeTask(task.remoteId!!)
            db.tasksDao().completeTask(task.localId!!)
        } else {
            db.tasksDao().completeTask(task.localId!!)
        }
    }

    private suspend fun syncTasks() {
        db.tasksDao().getUnsynchedTasks().forEach { entity ->
            try {
                val dto = entity.toDtoWithRemote(db)
                val newRemoteId = taskService.createTask(dto)
                db.tasksDao().updateTask(
                    entity.copy(remoteId = newRemoteId, isSynced = true)
                )
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun TaskEntity.toDtoWithRemote(db: AppDatabase): TaskDTO {
        val remoteProjId = projectId?.let { id ->
            db.projectsDao().getByLocalId(id)?.remoteId
        }
        return toDTO(remoteProjId)
    }

    private suspend fun ensureProjectSynced(localProjId: Long): Long? {
        val dao = db.projectsDao()
        val proj = dao.getByLocalId(localProjId) ?: return null

        proj.remoteId?.let { return it }

        if (!NetworkUtils.isNetworkAvailable(context)) return null

        val newRemote = projectService.createProject(proj.toDTO())
        dao.upsert(proj.copy(remoteId = newRemote, isSynced = true))
        return newRemote
    }

    override suspend fun deleteAllTasks() = withContext(Dispatchers.IO) {
        val allTasks = db.tasksDao().debugDump()

        if (NetworkUtils.isNetworkAvailable(context)) {
            allTasks.mapNotNull { it.remoteId }
                .distinct()
                .forEach { remoteId ->
                    try {
                        taskService.deleteTask(remoteId)
                    } catch (e: Exception) {
                        Log.w("TaskRepository", "Remote delete failed for id=$remoteId", e)
                    }
                }
        }

        db.tasksDao().clearAll()
    }


    override suspend fun getTasksByDate(date: Instant): List<Task> = withContext(Dispatchers.IO) {
        db.tasksDao().getTasksByDate(date).map { it.toDomain() }
    }

}