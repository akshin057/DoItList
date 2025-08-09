package com.example.doitlist.di

import android.content.Context
import androidx.room.Room
import com.example.doitlist.data.local.AppDatabase
import com.example.doitlist.utils.Converters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "doitlist-db"
        ).addTypeConverter(Converters())
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideTasksDao(db: AppDatabase) = db.tasksDao()

    @Provides
    fun provideProjectsDao(db: AppDatabase) = db.projectsDao()

    @Provides
    fun provideRoutinesDao(db: AppDatabase) = db.routinesDao()

    @Provides
    fun provideRoutineLogDao(db: AppDatabase) = db.routineLogsDao()

}