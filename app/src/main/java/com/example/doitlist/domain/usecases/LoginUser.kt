package com.example.doitlist.domain.usecases

import com.example.doitlist.domain.repository.UserRepository
import javax.inject.Inject

class LoginUser @Inject constructor(
    private val repo: UserRepository
) {
    suspend fun invoke(login: String, password: String) = repo.login(login, password)
}