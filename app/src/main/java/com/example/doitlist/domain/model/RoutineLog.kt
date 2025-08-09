package com.example.doitlist.domain.model

import kotlinx.datetime.LocalDate

data class RoutineLog(
    val localId: Long?,
    val remoteId: Long?,
    val routineId: Long,
    val doneAt: LocalDate
)