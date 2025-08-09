package com.example.doitlist.data.repository

import android.content.Context
import com.example.doitlist.data.local.AppDatabase
import com.example.doitlist.data.remote.routine.RoutineService
import com.example.doitlist.domain.model.Routine
import com.example.doitlist.domain.repository.RoutineRepository
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
import java.lang.IllegalArgumentException
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: AppDatabase,
    private val routineService: RoutineService
) : RoutineRepository {

    override suspend fun refreshRoutines() = withContext(Dispatchers.IO) {
        if (!NetworkUtils.isNetworkAvailable(context)) return@withContext

        syncRoutines()

        val remote = routineService.getRoutines().map { it.toEntity() }

        db.routinesDao().upsertAll(remote)

        val routinesFromDb = db.routinesDao().getAllRoutines()

        routinesFromDb.forEach { routine ->
            val todayLog = db.routineLogsDao().getTodayRoutineLog(
                routine.localId!!,
                routine.remoteId,
                Clock.System.todayIn(TimeZone.currentSystemDefault())
            )
            if (todayLog != null && todayLog.isSynced) {
                routine.isCompleted = true
            } else {
                routine.isCompleted = false
            }
            db.routinesDao().updateRoutine(routine)
        }
    }

    override fun observeRoutines(): Flow<List<Routine>> =
        db.routinesDao().observeRoutines()
            .map { list -> list.map { it.toDomain() }.sortedBy { it.isCompleted } }

    override suspend fun createRoutine(routine: Routine) = withContext(Dispatchers.IO) {
        val localId = db.routinesDao().addRoutine(routine.toEntity(isSynced = false))

        if (NetworkUtils.isNetworkAvailable(context)) {
            try {
                val remoteId = routineService.createRoutine(routine.toDTO())
                db.routinesDao().updateRoutine(
                    routine.copy(
                        remoteId = remoteId, localId = localId
                    ).toEntity(isSynced = true)
                )
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun updateRoutine(routine: Routine) {
        db.routinesDao().updateRoutine(routine.toEntity())

        if (NetworkUtils.isNetworkAvailable(context) && routine.remoteId != null) {
            try {
                routineService.updateRoutine(routine.remoteId, routine.toDTO())
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun completeRoutine(routine: Routine) = withContext(Dispatchers.IO) {
        db.routinesDao().updateRoutine(routine.copy(isCompleted = true).toEntity())
    }

    override suspend fun unCompleteRoutine(routine: Routine) = withContext(Dispatchers.IO) {
        val entity = routine.copy(isCompleted = false).toEntity()
        println(entity)
        db.routinesDao().updateRoutine(entity)
    }

    override suspend fun unCompleteAllRoutines() {
        db.routinesDao().getRoutines().forEach { entity ->
            db.routinesDao().updateRoutine(entity.copy(isCompleted = false))
        }
    }

    override suspend fun deleteRoutine(localId: Long) = withContext(Dispatchers.IO) {
        val entity = db.routinesDao().getByLocalId(localId)
            ?: throw IllegalArgumentException("Задача с localId=$localId")
        db.routinesDao().deleteRoutine(localId)

        if (NetworkUtils.isNetworkAvailable(context) && entity.remoteId != null) {
            try {
                routineService.deleteRoutine(entity.remoteId)
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    override suspend fun deleteAllRoutines() = withContext(Dispatchers.IO) {
        db.routinesDao().clearAll()
    }

    private suspend fun syncRoutines() {
        db.routinesDao().getUnsynchedRoutine().forEach { routine ->
            try {
                val remoteId = routineService.createRoutine(routine.toDTO())
                db.routinesDao().updateRoutine(
                    routine.copy(
                        remoteId = remoteId,
                        isSynced = true
                    )
                )
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}