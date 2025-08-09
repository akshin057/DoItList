package com.example.doitlist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.doitlist.data.local.models.RoutineEntity
import com.example.doitlist.domain.model.Routine
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRoutine(routineEntity: RoutineEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRoutine(routineEntity: RoutineEntity)

    @Query("DELETE FROM routines")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routines: List<RoutineEntity>)

    @Query("SELECT * FROM routines")
    suspend fun getRoutines(): List<RoutineEntity>

    @Query("SELECT * FROM routines WHERE localId = :localId")
    suspend fun getByLocalId(localId: Long): RoutineEntity?

    @Query("SELECT * FROM routines")
    fun observeRoutines(): Flow<List<RoutineEntity>>

    @Query("DELETE FROM routines WHERE localId = :localId")
    suspend fun deleteRoutine(localId: Long)

    @Upsert
    suspend fun upsert(routine: RoutineEntity)

    @Upsert
    suspend fun upsertAll(routines: List<RoutineEntity>)

    @Query("SELECT * FROM routines WHERE isSynced = 0 AND remoteId IS null")
    suspend fun getUnsynchedRoutine(): List<RoutineEntity>

    @Query("SELECT * FROM routines")
    suspend fun getAllRoutines(): List<RoutineEntity>

}