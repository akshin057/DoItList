package com.example.doitlist.data.local.models

import androidx.room.Embedded
import androidx.room.Relation

data class ProjectWithTasks(
    @Embedded val project: ProjectEntity,
    @Relation(
        parentColumn = "localId",
        entityColumn = "projectId"
    )
    val tasks: List<TaskEntity>
)