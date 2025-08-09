package com.example.doitlist.data.local.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Entity(
    tableName = "routines",
    indices = [Index(value = ["remoteId"], unique = true)]
)
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long? = null,
    val remoteId: Long?,
    val name: String,
    val description: String?,
    val startDate: LocalDate,
    val reminderTime: LocalTime? = null,
    val isSynced: Boolean = false,
    var isCompleted: Boolean = false
)