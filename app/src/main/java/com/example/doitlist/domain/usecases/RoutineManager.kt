package com.example.doitlist.domain.usecases

import com.example.doitlist.domain.model.Routine
import com.example.doitlist.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoutineManager @Inject constructor(
    private val repo: RoutineRepository
) {
    suspend fun create(routine: Routine) = repo.createRoutine(routine = routine)

    suspend fun update(routine: Routine) = repo.updateRoutine(routine = routine)

    suspend fun delete(localId: Long) = repo.deleteRoutine(localId)

    suspend fun refresh() = repo.refreshRoutines()

    suspend fun deleteAll() = repo.deleteAllRoutines()

    fun observeRoutines(): Flow<List<Routine>> = repo.observeRoutines()

    suspend fun complete(routine: Routine) = repo.completeRoutine(routine)

    suspend fun unComplete(routine: Routine) = repo.unCompleteRoutine(routine)

    suspend fun unCompleteAllRoutines() = repo.unCompleteAllRoutines()
}