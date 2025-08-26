package com.example.doitlist.domain.repository

import com.example.doitlist.data.remote.dto.UserDTO

interface UserRepository {
    suspend fun register(user: UserDTO): String

    suspend fun login(login: String, password: String): String

    suspend fun getUserSettings(): UserDTO?

    suspend fun changeName(newSurname: String, newName: String, newLastName: String): UserDTO?

    suspend fun changeLogin(newLogin: String): UserDTO?

    suspend fun changePassword(newPassword: String): UserDTO?

    suspend fun changeEmail(newEmail: String): UserDTO?

}