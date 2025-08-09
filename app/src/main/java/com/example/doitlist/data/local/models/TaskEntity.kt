package com.example.doitlist.data.local.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["remoteId"], unique = true),
        Index("projectId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["localId"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long? = null,
    val remoteId: Long?,
    val name: String,
    val description: String? = null,
    val projectId: Long? = null,
    val recurrenceId: Long? = null,
    val isDone: Boolean = false,
    val importance: Int,
    val startDate: Instant,
    val endDate: Instant? = null,
    val completedAt: Instant? = null,
    val reminderTime: LocalTime? = null,
    val updatedAt: Instant? = null,
    val isSynced: Boolean = false
)