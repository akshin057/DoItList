package com.example.doitlist.domain.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

data class Task(
    val remoteId: Long?,
    val localId: Long?,
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
