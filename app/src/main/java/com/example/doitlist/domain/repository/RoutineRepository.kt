package com.example.doitlist.domain.repository

import com.example.doitlist.domain.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {

    fun observeRoutines(): Flow<List<Routine>>

    suspend fun createRoutine(routine: Routine)

    suspend fun updateRoutine(routine: Routine)

    suspend fun deleteRoutine(id: Long)

    suspend fun deleteAllRoutines()

    suspend fun refreshRoutines()

    suspend fun completeRoutine(routine: Routine)

    suspend fun unCompleteRoutine(routine: Routine)

    suspend fun unCompleteAllRoutines()
}