package com.example.doitlist.domain.model

import kotlinx.datetime.LocalDate

data class Project(
    val remoteId: Long?,
    val localId: Long?,
    val name: String,
    val description: String? = null,
    val startDate: LocalDate,
    val endDate: LocalDate? = null
)
