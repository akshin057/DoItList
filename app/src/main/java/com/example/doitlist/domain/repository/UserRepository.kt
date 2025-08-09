package com.example.doitlist.domain.repository

import com.example.doitlist.data.remote.dto.UserDTO

interface UserRepository {
    suspend fun register(user: UserDTO): String
    suspend fun login(login: String, password: String): String
}