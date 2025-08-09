package com.example.doitlist.utils.mappers

import com.example.doitlist.data.local.models.TaskEntity
import com.example.doitlist.data.remote.dto.TaskDTO
import com.example.doitlist.domain.model.Task

fun Task.toEntity(
    isSynced: Boolean = (this.remoteId != null),
): TaskEntity = TaskEntity(
    localId = this.localId,
    remoteId = this.remoteId,
    name = this.name,
    description = this.description,
    projectId = this.projectId,
    recurrenceId = this.recurrenceId,
    isDone = this.isDone,
    importance = this.importance,
    startDate = this.startDate,
    endDate = this.endDate,
    completedAt = this.completedAt,
    reminderTime = this.reminderTime,
    updatedAt = this.updatedAt,
    isSynced = isSynced
)

fun TaskEntity.toDomain(): Task = Task(
    remoteId = this.remoteId,
    localId = this.localId,
    name = this.name,
    description = this.description,
    projectId = this.projectId,
    recurrenceId = this.recurrenceId,
    isDone = this.isDone,
    importance = this.importance,
    startDate = this.startDate,
    endDate = this.endDate,
    completedAt = this.completedAt,
    reminderTime = this.reminderTime,
    updatedAt = this.updatedAt
)

fun TaskDTO.toEntity(localProjectId: Long?): TaskEntity = TaskEntity(
    localId      = null,
    remoteId     = id,
    name         = name,
    description  = description,
    projectId    = localProjectId,
    recurrenceId = recurrenceId,
    isDone       = isDone,
    importance   = importance,
    startDate    = startDate,
    endDate      = endDate,
    completedAt  = completedAt,
    reminderTime = reminderTime,
    updatedAt    = updatedAt,
    isSynced     = true
)


fun TaskDTO.toDomain(
    localId: Long? = null
): Task = Task(
    remoteId = id,
    localId = localId,
    name = this.name,
    description = this.description,
    projectId = this.projectId,
    recurrenceId = this.recurrenceId,
    isDone = this.isDone,
    importance = this.importance,
    startDate = this.startDate,
    endDate = this.endDate,
    completedAt = this.completedAt,
    reminderTime = this.reminderTime,
    updatedAt = this.updatedAt
)

fun TaskEntity.toDTO(remoteProjectId: Long?): TaskDTO = TaskDTO(
    id           = remoteId,
    name         = name,
    description  = description,
    projectId    = remoteProjectId,
    recurrenceId = recurrenceId,
    isDone       = isDone,
    importance   = importance,
    startDate    = startDate,
    endDate      = endDate,
    completedAt  = completedAt,
    reminderTime = reminderTime,
    updatedAt    = updatedAt
)

fun Task.toDTO(remoteProjectId: Long?): TaskDTO = TaskDTO(
    id = remoteId,
    name = this.name,
    description = this.description,
    projectId = remoteProjectId,
    recurrenceId = this.recurrenceId,
    isDone = this.isDone,
    importance = this.importance,
    startDate = this.startDate,
    endDate = this.endDate,
    completedAt = this.completedAt,
    reminderTime = this.reminderTime,
    updatedAt = this.updatedAt
)
