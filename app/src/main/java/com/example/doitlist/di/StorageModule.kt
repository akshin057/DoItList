package com.example.doitlist.di

import android.content.Context
import android.content.SharedPreferences
import com.example.doitlist.data.local.DateStorage
import com.example.doitlist.data.local.TokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(
        @ApplicationContext context: Context
    ): SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideTokenStorage(prefs: SharedPreferences) = TokenStorage(prefs)

    @Provides
    @Singleton
    fun provideDateStorage(prefs: SharedPreferences) = DateStorage(prefs)
}