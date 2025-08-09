package com.example.doitlist.presentation.register

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doitlist.domain.usecases.LoginUser
import com.example.doitlist.domain.usecases.RegisterUser
import com.example.doitlist.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUser: RegisterUser
) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun onRegister(
        surname: String,
        name: String,
        lastName: String,
        login: String,
        email: String,
        password: String
    ) = viewModelScope.launch {
        uiState = UiState.Loading
        uiState = try {
            val token = registerUser.invoke(surname = surname, name = name, lastName = lastName, login = login, email = email, password = password)
            if (token.isBlank()) throw IllegalStateException("Empty token")
            UiState.Success(token)
        } catch (t: Throwable) {
            Log.d("RegisterViewModel", "register failed", t)
            UiState.Error(t)
        }
    }

}

