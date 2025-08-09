package com.example.doitlist.utils.mappers

import androidx.compose.foundation.isSystemInDarkTheme
import com.example.doitlist.data.local.models.RoutineEntity
import com.example.doitlist.data.remote.dto.RoutineDTO
import com.example.doitlist.domain.model.Routine

fun Routine.toEntity(
    isSynced: Boolean = (this.remoteId != null),
    remoteId: Long? = this.remoteId,
    localId: Long? = this.localId,
    isCompleted: Boolean = this.isCompleted
): RoutineEntity =
    RoutineEntity(
        remoteId = remoteId,
        localId = localId,
        name = name,
        description = description,
        startDate = startDate,
        reminderTime = reminderTime,
        isSynced = isSynced,
        isCompleted = isCompleted
    )

fun Routine.toDTO(): RoutineDTO =
    RoutineDTO(
        id = remoteId,
        name = name,
        description = description,
        startDate = startDate,
        reminderTime = reminderTime,
        userId = null
    )

fun RoutineEntity.toDTO(): RoutineDTO =
    RoutineDTO(
        id = remoteId,
        name = name,
        description = description,
        startDate = startDate,
        reminderTime = reminderTime,
        userId = null
    )

fun RoutineEntity.toDomain(): Routine =
    Routine(
        remoteId = remoteId,
        localId = localId,
        name = name,
        description = description,
        startDate = startDate,
        reminderTime = reminderTime,
        isCompleted = isCompleted
    )

fun RoutineDTO.toDomain(localId: Long?): Routine =
    Routine(
        remoteId = id,
        localId = localId,
        name = name,
        description = description,
        startDate = startDate,
        reminderTime = reminderTime
    )

fun RoutineDTO.toEntity(): RoutineEntity =
    RoutineEntity(
        localId = null,
        remoteId = id,
        name = name,
        description = description,
        startDate = startDate,
        reminderTime = reminderTime,
        isCompleted = false
    )
