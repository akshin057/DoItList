package com.example.doitlist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.doitlist.data.local.dao.ProjectsDAO
import com.example.doitlist.data.local.dao.RoutineDAO
import com.example.doitlist.data.local.dao.RoutineLogDAO
import com.example.doitlist.data.local.dao.TasksDAO
import com.example.doitlist.data.local.models.ProjectEntity
import com.example.doitlist.data.local.models.RoutineEntity
import com.example.doitlist.data.local.models.RoutineLogEntity
import com.example.doitlist.data.local.models.TaskEntity
import com.example.doitlist.utils.Converters

@Database(
    entities = [TaskEntity::class, ProjectEntity::class, RoutineEntity::class, RoutineLogEntity::class],
    version = 10,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tasksDao(): TasksDAO
    abstract fun projectsDao(): ProjectsDAO
    abstract fun routinesDao(): RoutineDAO
    abstract fun routineLogsDao(): RoutineLogDAO
}