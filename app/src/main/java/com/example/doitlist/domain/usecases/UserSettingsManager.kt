package com.example.doitlist.domain.usecases

import com.example.doitlist.data.remote.dto.UserDTO
import com.example.doitlist.domain.repository.UserRepository
import javax.inject.Inject

class UserSettingsManager @Inject constructor(
    private val repo: UserRepository
) {

    suspend fun getUser(): UserDTO? = repo.getUserSettings()

    suspend fun changeEmail(newEmail: String): UserDTO? = repo.changeEmail(newEmail)

    suspend fun changeLogin(newLogin: String): UserDTO? = repo.changeLogin(newLogin)

    suspend fun changePassword(newPassword: String): UserDTO? = repo.changePassword(newPassword)

    suspend fun changeName(newSurname: String, newName: String, newLastName: String): UserDTO? =
        repo.changeName(newSurname = newSurname, newName = newName, newLastName = newLastName)
}