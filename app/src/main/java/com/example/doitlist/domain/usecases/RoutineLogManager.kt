package com.example.doitlist.domain.usecases

import com.example.doitlist.domain.model.Routine
import com.example.doitlist.domain.model.RoutineLog
import com.example.doitlist.domain.repository.RoutineLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoutineLogManager @Inject constructor(
    private val repo: RoutineLogRepository
) {
    suspend fun create(routineLog: RoutineLog) = repo.createRoutineLog(routineLog)

    suspend fun delete(localId: Long) = repo.deleteRoutineLog(localId)

    fun observeRoutineLogs(): Flow<List<RoutineLog>> = repo.observeRoutineLogs()

    suspend fun refresh() = repo.refreshRoutineLogs()

    suspend fun getTodayRoutineLog(routineId: Long, routineRemoteId: Long?): RoutineLog? =
        repo.getTodayRoutineLog(routineId = routineId, routineRemoteId = routineRemoteId)

}