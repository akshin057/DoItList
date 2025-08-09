package com.example.doitlist.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Serializable
data class RoutineDTO(
    val id: Long?,
    val name: String,
    val description: String? = null,
    val userId: Long?,
    val startDate: LocalDate,
    val reminderTime: LocalTime? = null
)
