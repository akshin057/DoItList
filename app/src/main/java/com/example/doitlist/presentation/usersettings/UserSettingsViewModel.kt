package com.example.doitlist.presentation.usersettings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doitlist.data.remote.dto.UserDTO
import com.example.doitlist.domain.usecases.UserSettingsManager
import com.example.doitlist.utils.UiState
import com.example.doitlist.utils.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserSettingsViewModel @Inject constructor(
    private val manager: UserSettingsManager
) : ViewModel() {

    var uiState by mutableStateOf<UserUiState>(UserUiState.Idle)
        private set

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            uiState = UserUiState.Loading
            uiState = try {
                val user = manager.getUser()
                if (user != null) UserUiState.Success(user)
                else UserUiState.Error("Пользователь не найден")
            } catch (t: Throwable) {
                UserUiState.Error(t.message)
            }
        }
    }

    fun changeLogin(newLogin: String) {
        viewModelScope.launch {
            uiState = UserUiState.Loading
            uiState = try {
                val user = manager.changeLogin(newLogin)

                if (user != null) UserUiState.Success(user = user)
                else UserUiState.Error("Логин занят")
            } catch (t: Throwable) {
                UserUiState.Error(t.message)
            }
        }
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch {
            uiState = UserUiState.Loading
            uiState = try {
                val user = manager.changePassword(newPassword)

                if (user != null) UserUiState.Success(user = user)
                else UserUiState.Error("Не удалось сменить")
            } catch (t: Throwable) {
                UserUiState.Error(t.message)
            }
        }
    }

    fun changeEmail(newEmail: String) {
        viewModelScope.launch {
            uiState = UserUiState.Loading
            uiState = try {
                val user = manager.changeEmail(newEmail)

                if (user != null) UserUiState.Success(user = user)
                else UserUiState.Error("Не удалось сменить")
            } catch (t: Throwable) {
                UserUiState.Error(t.message)
            }
        }
    }

    fun changeName(newSurname: String, newName: String, newLastName: String) {
        viewModelScope.launch {
            uiState = UserUiState.Loading
            uiState = try {
                val user = manager.changeName(
                    newSurname = newSurname,
                    newName = newName,
                    newLastName = newLastName
                )

                if (user != null) UserUiState.Success(user = user)
                else UserUiState.Error("Не удалось сменить")
            } catch (t: Throwable) {
                UserUiState.Error(t.message)
            }
        }
    }

}