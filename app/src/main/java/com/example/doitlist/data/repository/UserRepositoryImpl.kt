package com.example.doitlist.data.repository

import com.example.doitlist.data.local.TokenStorage
import com.example.doitlist.data.remote.dto.UserDTO
import com.example.doitlist.data.remote.user.UserService
import com.example.doitlist.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserService,
    private val tokenStorage: TokenStorage
) : UserRepository {

    override suspend fun login(login: String, password: String): String {
        val token = api.loginUser(login, password)
        if (token.isNotBlank()) tokenStorage.save(token)
        return token
    }

    override suspend fun register(user: UserDTO): String {
        val token = api.registerUser(user)
        if (token.isNotBlank()) tokenStorage.save(token)
        return token
    }

    override suspend fun getUserSettings(): UserDTO? {
        val user = api.getUserSettings()
        if (user != null) return user
        else return null
    }

    override suspend fun changePassword(newPassword: String): UserDTO? =
        api.changePassword(newPassword)

    override suspend fun changeEmail(newEmail: String): UserDTO? =
        api.changeEmail(newEmail)

    override suspend fun changeName(newSurname: String, newName: String, newLastName: String): UserDTO? =
        api.changeName(surname = newSurname, name = newName, lastName = newLastName)

    override suspend fun changeLogin(newLogin: String) : UserDTO? =
        api.changeLogin(newLogin)

}