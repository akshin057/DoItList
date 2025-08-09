package com.example.doitlist.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(
    val login: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String
)