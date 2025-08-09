package com.example.doitlist.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.doitlist.data.local.models.RoutineLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface RoutineLogDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLog(routineLogEntity: RoutineLogEntity): Long

    @Query("DELETE FROM routine_logs WHERE localId =:id")
    suspend fun deleteLog(id: Long)

    @Query("SELECT * FROM routine_logs WHERE localId =:id")
    suspend fun getByLocalId(id: Long): RoutineLogEntity

    @Query("SELECT * FROM routine_logs WHERE (routineId =:routineId OR routineId =:remoteId) AND doneAt = :date LIMIT 1")
    suspend fun getTodayRoutineLog(
        routineId: Long,
        remoteId: Long?,
        date: LocalDate
    ): RoutineLogEntity?

    @Update
    suspend fun updateLog(routineLogEntity: RoutineLogEntity)

    @Query("SELECT * FROM routine_logs WHERE doneAt = :day")
    fun observeDone(day: LocalDate): Flow<List<RoutineLogEntity>>

    @Query("SELECT * FROM routine_logs WHERE isSynced = 0 AND remoteId IS Null")
    suspend fun getUnsyncedLogs(): List<RoutineLogEntity>

    @Upsert
    suspend fun upsertAll(routineLogs: List<RoutineLogEntity>)
}