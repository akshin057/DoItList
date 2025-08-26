package com.example.doitlist.data.remote.user

import com.example.doitlist.data.remote.dto.UserDTO

interface UserService {
    suspend fun registerUser(body: UserDTO): String

    suspend fun loginUser(login: String, password: String): String

    suspend fun getUserSettings(): UserDTO?

    suspend fun changeEmail(newEmail: String): UserDTO?

    suspend fun changeLogin(newLogin: String): UserDTO?

    suspend fun changePassword(newPassword: String): UserDTO?

    suspend fun changeName(surname: String, name: String, lastName: String): UserDTO?

}