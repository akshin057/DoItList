package com.example.doitlist.utils.mappers

import com.example.doitlist.data.local.models.ProjectEntity
import com.example.doitlist.data.remote.dto.ProjectDTO
import com.example.doitlist.domain.model.Project

fun Project.toDTO(): ProjectDTO =
    ProjectDTO(
        id = remoteId,
        name = name,
        description = description,
        startDate = startDate,
        endDate = endDate,
        userId = null
    )

fun ProjectDTO.toDomain(localId: Long?): Project =
    Project(
        remoteId = id,
        localId = localId,
        name = name,
        description = description,
        startDate = startDate,
        endDate = endDate
    )

fun ProjectDTO.toEntity(): ProjectEntity =
    ProjectEntity(
        localId = null,
        remoteId = id,
        name = name,
        description = description,
        startDate = startDate,
        endDate = endDate
    )

fun ProjectEntity.toDTO(): ProjectDTO =
    ProjectDTO(
        id = remoteId,
        name = name,
        description = description,
        startDate = startDate,
        endDate = endDate,
        userId = null
    )

fun Project.toEntity(
    isSynced: Boolean = (this.remoteId != null),
    remoteId: Long? = this.remoteId,
    localId: Long? = this.localId
): ProjectEntity =
    ProjectEntity(
        localId = localId,
        remoteId = remoteId,
        name = name,
        description = description,
        startDate = startDate,
        endDate = endDate,
        isSynced = isSynced
    )


fun ProjectEntity.toDomain(): Project =
    Project(
        localId = localId,
        remoteId = remoteId,
        name = name,
        description = description,
        startDate = startDate,
        endDate = endDate
    )