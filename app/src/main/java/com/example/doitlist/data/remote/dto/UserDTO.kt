package com.example.doitlist.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val surname: String,
    val name: String,
    val lastName: String,
    val login: String,
    val email: String,
    val password: String
)