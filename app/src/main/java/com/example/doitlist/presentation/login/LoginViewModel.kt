package com.example.doitlist.presentation.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doitlist.domain.usecases.LoginUser
import com.example.doitlist.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUser: LoginUser
) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun onLogin(login: String, pass: String) = viewModelScope.launch {
        uiState = UiState.Loading
        uiState = try {
            val token = loginUser.invoke(login, pass)
            if (token.isBlank()) throw IllegalStateException("Empty token")
            UiState.Success(token)
        } catch (t: Throwable) {
            Log.d("LoginViewModel", "auth failed", t)
            UiState.Error(t)
        }
    }

}