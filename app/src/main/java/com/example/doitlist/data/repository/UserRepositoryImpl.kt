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

}