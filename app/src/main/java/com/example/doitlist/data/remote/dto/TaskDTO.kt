package com.example.doitlist.data.remote.dto

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class TaskDTO(
    val id: Long?,
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
    val updatedAt: Instant? = null
)

@kotlinx.serialization.Serializable
data class IdResponse(val id: Long)