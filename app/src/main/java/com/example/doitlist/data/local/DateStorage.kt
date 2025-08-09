package com.example.doitlist.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class DateStorage @Inject constructor(
    private val prefs: SharedPreferences
){
    private companion object {
        const val KEY_LAST_REFRESH_DATE = "last_refresh_date"
    }

    fun saveLastRefreshDate(date: String) {
        prefs.edit() { putString(KEY_LAST_REFRESH_DATE, date) }
    }

    fun getLastRefreshDate(): String? = prefs.getString(KEY_LAST_REFRESH_DATE, null)

    fun clearLastRefreshDate() {
        prefs.edit() { remove(KEY_LAST_REFRESH_DATE) }
    }

}