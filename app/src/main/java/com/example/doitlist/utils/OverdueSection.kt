package com.example.doitlist.utils

import com.example.doitlist.domain.model.Task
import kotlinx.datetime.LocalDate

data class OverdueSection(
    val date: LocalDate,
    val tasks: List<Task>
)
