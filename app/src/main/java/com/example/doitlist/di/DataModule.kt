package com.example.doitlist.di

import com.example.doitlist.data.local.TokenStorage
import com.example.doitlist.data.remote.project.ProjectService
import com.example.doitlist.data.remote.project.ProjectServiceImpl
import com.example.doitlist.data.remote.routine.RoutineService
import com.example.doitlist.data.remote.routine.RoutineServiceImpl
import com.example.doitlist.data.remote.routinelogs.RoutineLogService
import com.example.doitlist.data.remote.routinelogs.RoutineLogServiceImpl
import com.example.doitlist.data.remote.task.TaskService
import com.example.doitlist.data.remote.task.TaskServiceImpl
import com.example.doitlist.data.remote.user.UserService
import com.example.doitlist.data.remote.user.UserServiceImpl
import com.example.doitlist.data.repository.ProjectRepositoryImpl
import com.example.doitlist.data.repository.RoutineLogRepositoryImpl
import com.example.doitlist.data.repository.RoutineRepositoryImpl
import com.example.doitlist.data.repository.TaskRepositoryImpl
import com.example.doitlist.domain.repository.UserRepository
import com.example.doitlist.data.repository.UserRepositoryImpl
import com.example.doitlist.domain.repository.ProjectRepository
import com.example.doitlist.domain.repository.RoutineLogRepository
import com.example.doitlist.domain.repository.RoutineRepository
import com.example.doitlist.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserService(
        impl: UserServiceImpl
    ): UserService

    @Binds
    @Singleton
    abstract fun bindTaskService(
        impl: TaskServiceImpl,
    ): TaskService

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindProjectRepository(
        impl: ProjectRepositoryImpl
    ): ProjectRepository

    @Binds
    @Singleton
    abstract fun bindProjectService(
        impl: ProjectServiceImpl
    ): ProjectService

    @Binds
    @Singleton
    abstract fun bindRoutineService(
        impl: RoutineServiceImpl
    ): RoutineService

    @Binds
    @Singleton
    abstract fun bindRoutineRepository(
        impl: RoutineRepositoryImpl
    ): RoutineRepository

    @Binds
    @Singleton
    abstract fun bindRoutineLogRepository(
        impl: RoutineLogRepositoryImpl
    ): RoutineLogRepository

    @Binds
    @Singleton
    abstract fun bindRoutineLogService(
        impl: RoutineLogServiceImpl
    ): RoutineLogService
}