package com.example.doitlist.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "routine_logs",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["localId"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("routineId"), Index("localId")]
)
data class RoutineLogEntity(
    @PrimaryKey val localId: Long? = null,
    val remoteId: Long? = null,
    val routineId: Long,
    val doneAt: LocalDate,
    val isSynced: Boolean = false
)