package com.example.doitlist.data.remote.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class RoutineLogDTO(
    val id: Long?,
    val routineId: Long,
    val doneAt: LocalDate
)
