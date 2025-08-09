package com.example.doitlist.domain.usecases

import com.example.doitlist.data.remote.dto.UserDTO
import com.example.doitlist.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUser @Inject constructor(
    private val repo: UserRepository
) {
    suspend fun invoke(
        surname: String,
        name: String,
        lastName: String,
        login: String,
        email: String,
        password: String
    ) = repo.register (
        UserDTO(
            surname = surname,
            name = name,
            lastName = lastName,
            login = login,
            email = email,
            password = password
        )
    )
}