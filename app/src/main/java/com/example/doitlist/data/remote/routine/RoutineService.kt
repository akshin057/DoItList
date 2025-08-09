package com.example.doitlist.data.remote.routine

import com.example.doitlist.data.remote.dto.RoutineDTO

interface RoutineService {

    suspend fun createRoutine(routineDTO: RoutineDTO): Long

    suspend fun updateRoutine(id: Long, routineDTO: RoutineDTO): Boolean

    suspend fun deleteRoutine(id: Long): Boolean

    suspend fun getRoutines(): List<RoutineDTO>
}