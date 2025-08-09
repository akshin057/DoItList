package com.example.doitlist.data.local.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

@Entity(
    tableName = "projects",
    indices = [Index(value = ["remoteId"], unique = true)]
)
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long? = null,
    val remoteId: Long?,
    val name: String,
    val description: String?,
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val isSynced: Boolean = false
)