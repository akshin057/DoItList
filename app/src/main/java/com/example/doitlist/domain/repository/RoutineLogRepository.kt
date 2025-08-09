package com.example.doitlist.domain.repository

import com.example.doitlist.domain.model.RoutineLog
import kotlinx.coroutines.flow.Flow

interface RoutineLogRepository {

    fun observeRoutineLogs(): Flow<List<RoutineLog>>

    suspend fun createRoutineLog(routineLog: RoutineLog)

    suspend fun deleteRoutineLog(id: Long)

    suspend fun refreshRoutineLogs()

    suspend fun getTodayRoutineLog(routineId: Long, routineRemoteId: Long?): RoutineLog?
}