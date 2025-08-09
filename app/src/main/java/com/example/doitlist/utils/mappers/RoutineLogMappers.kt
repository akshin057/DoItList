package com.example.doitlist.utils.mappers

import com.example.doitlist.data.local.models.RoutineLogEntity
import com.example.doitlist.data.remote.dto.RoutineLogDTO
import com.example.doitlist.domain.model.RoutineLog

fun RoutineLogEntity.toDTO(remoteRoutineId: Long?): RoutineLogDTO =
    RoutineLogDTO(
        id = remoteId,
        routineId = remoteRoutineId ?: routineId,
        doneAt = doneAt
    )

fun RoutineLogEntity.toDomain(): RoutineLog =
    RoutineLog(
        localId = localId,
        remoteId = remoteId,
        routineId = routineId,
        doneAt = doneAt
    )

fun RoutineLogDTO.toEntity(remoteRoutineId: Long?): RoutineLogEntity =
    RoutineLogEntity(
        localId = null,
        remoteId = id,
        routineId = remoteRoutineId ?: routineId,
        doneAt = doneAt
    )

fun RoutineLogDTO.toDomain(): RoutineLog =
    RoutineLog(
        localId = null,
        remoteId = id,
        routineId = routineId,
        doneAt = doneAt
    )

fun RoutineLog.toEntity(
    isSynced: Boolean = (this.remoteId != null),
    remoteId: Long? = this.remoteId,
    localId: Long? = this.localId
): RoutineLogEntity =
    RoutineLogEntity(
        localId = localId,
        remoteId = remoteId,
        routineId = routineId,
        doneAt = doneAt,
        isSynced = isSynced
    )

fun RoutineLog.toDTO(): RoutineLogDTO =
    RoutineLogDTO(
        id = remoteId,
        routineId = routineId,
        doneAt = doneAt
    )
