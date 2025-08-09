package com.example.doitlist.data.repository

import android.content.Context
import com.example.doitlist.data.local.AppDatabase
import com.example.doitlist.data.local.models.RoutineLogEntity
import com.example.doitlist.data.remote.dto.RoutineLogDTO
import com.example.doitlist.data.remote.routine.RoutineService
import com.example.doitlist.data.remote.routinelogs.RoutineLogService
import com.example.doitlist.domain.model.RoutineLog
import com.example.doitlist.domain.repository.RoutineLogRepository
import com.example.doitlist.utils.NetworkUtils
import com.example.doitlist.utils.mappers.toDTO
import com.example.doitlist.utils.mappers.toDomain
import com.example.doitlist.utils.mappers.toEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject
import kotlin.collections.map

class RoutineLogRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val routineLogService: RoutineLogService,
    private val db: AppDatabase,
    private val routineService: RoutineService,
) : RoutineLogRepository {

    override fun observeRoutineLogs(): Flow<List<RoutineLog>> =
        db.routineLogsDao().observeDone(Clock.System.todayIn(TimeZone.currentSystemDefault()))
            .map { list -> list.map { it.toDomain() } }

    override suspend fun createRoutineLog(routineLog: RoutineLog) = withContext(Dispatchers.IO) {
        var localId = db.routineLogsDao().addLog(routineLog.toEntity())

        val remoteRoutineId = ensureRoutineSynced(routineLog.routineId)

        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val remoteId = routineLogService.createRoutineLogs(
                    routineLog.copy(
                        routineId = remoteRoutineId ?: routineLog.routineId,
                    ).toDTO()
                )

                db.routineLogsDao().updateLog(
                    routineLog.copy(
                        remoteId = remoteId,
                        localId = localId
                    ).toEntity(isSynced = true)
                )
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun deleteRoutineLog(id: Long) = withContext(Dispatchers.IO) {
        val entity = db.routineLogsDao().getByLocalId(id)

        db.routineLogsDao().deleteLog(id)

        if (NetworkUtils.isNetworkAvailable(context) && entity.remoteId != null) {
            try {
                routineLogService.deleteRoutineLog(entity.remoteId)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun refreshRoutineLogs() = withContext(Dispatchers.IO) {
        if (!NetworkUtils.isNetworkAvailable(context)) return@withContext

        syncRoutineLogs()

        val remoteRoutine = routineService.getRoutines().map { it.toEntity() }

        db.routinesDao().upsertAll(remoteRoutine)

        val idMap = db.routinesDao()
            .getAllRoutines()
            .associate { it.remoteId!! to it.localId!! }

        val remoteRoutineLogs = routineLogService.getRoutineLogs().map { dto ->
            dto.toEntity(remoteRoutineId = dto.routineId.let { idMap[it] })
        }

        db.routineLogsDao().upsertAll(remoteRoutineLogs)
    }

    private suspend fun syncRoutineLogs() {
        db.routineLogsDao().getUnsyncedLogs().forEach { routineLog ->
            try {
                val remoteId = routineLogService.createRoutineLogs(routineLog.toDtoWithRemote())

                db.routineLogsDao().updateLog(
                    routineLog.copy(
                        remoteId = remoteId,
                        isSynced = true
                    )
                )
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun getTodayRoutineLog(routineId: Long, routineRemoteId: Long?): RoutineLog? {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return db.routineLogsDao().getTodayRoutineLog(routineId, routineRemoteId, today)?.toDomain()
    }

    private suspend fun RoutineLogEntity.toDtoWithRemote(): RoutineLogDTO {

        val remoteRoutineId = db.routinesDao().getByLocalId(routineId)?.remoteId

        return toDTO(remoteRoutineId ?: routineId)
    }

    private suspend fun ensureRoutineSynced(localRoutineId: Long): Long? {
        val routine = db.routinesDao().getByLocalId(localRoutineId) ?: return null

        routine.remoteId?.let { return it }

        if (!NetworkUtils.isNetworkAvailable(context)) return null

        val newRemote = routineService.createRoutine(routine.toDTO())
        db.routinesDao().upsert(routine.copy(remoteId = newRemote, isSynced = true))
        return newRemote
    }

}