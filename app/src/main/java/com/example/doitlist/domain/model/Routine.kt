package com.example.doitlist.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Routine(
    val remoteId: Long?,
    val localId: Long?,
    val name: String,
    val description: String? = null,
    val startDate: LocalDate,
    val reminderTime: LocalTime? = null,
    val isCompleted: Boolean = false
)