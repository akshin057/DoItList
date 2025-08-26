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

@Serializable
data class ChangeLoginDTO(
    val login: String
)

@Serializable
data class ChangeEmailDTO(
    val email: String
)

@Serializable
data class ChangeNameDTO(
    val surname: String,
    val name: String,
    val lastName: String
)

@Serializable
data class ChangePasswordDTO(
    val password: String
)