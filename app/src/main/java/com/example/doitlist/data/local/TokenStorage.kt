package com.example.doitlist.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class TokenStorage @Inject constructor(
    private val prefs: SharedPreferences
) {

    private companion object {
        const val KEY_TOKEN = "auth_token"
    }

    fun save(token: String) {
        prefs.edit() { putString(KEY_TOKEN, token) }
    }

    fun get(): String? = prefs.getString(KEY_TOKEN, null)

    fun clear() {
        prefs.edit() { remove(KEY_TOKEN) }
    }

}