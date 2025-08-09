package com.example.doitlist.data.remote.routinelogs

import com.example.doitlist.data.remote.dto.RoutineLogDTO
import java.time.LocalDate

interface RoutineLogService {

    suspend fun createRoutineLogs(routineLogDTO: RoutineLogDTO): Long

    suspend fun deleteRoutineLog(id: Long): Boolean

    suspend fun getCompletedToday(): List<Long>

    suspend fun getRoutineLogs() : List<RoutineLogDTO>
}