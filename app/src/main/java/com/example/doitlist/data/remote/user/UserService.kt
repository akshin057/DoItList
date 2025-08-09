package com.example.doitlist.data.remote.user

import com.example.doitlist.data.remote.dto.UserDTO

interface UserService {
    suspend fun registerUser(body: UserDTO): String

    suspend fun loginUser(login: String, password: String): String

}