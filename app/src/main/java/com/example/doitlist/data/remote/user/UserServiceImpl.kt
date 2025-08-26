package com.example.doitlist.data.remote.user

import com.example.doitlist.data.local.TokenStorage
import com.example.doitlist.data.remote.dto.AuthResponse
import com.example.doitlist.data.remote.dto.ChangeEmailDTO
import com.example.doitlist.data.remote.dto.ChangeLoginDTO
import com.example.doitlist.data.remote.dto.ChangeNameDTO
import com.example.doitlist.data.remote.dto.ChangePasswordDTO
import com.example.doitlist.data.remote.dto.LoginDTO
import com.example.doitlist.data.remote.dto.UserDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import javax.inject.Inject

class UserServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : UserService {

    private fun HttpRequestBuilder.withAuthHeader() {
        tokenStorage.get()?.let { token ->
            println(token)
            header("Bearer-Authorisation", token)
        }
    }

    override suspend fun registerUser(body: UserDTO): String {
        val resp = client.post("/register") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

        return when (resp.status) {
            HttpStatusCode.OK -> {
                val auth = resp.body<AuthResponse>()
                auth.token
            }

            HttpStatusCode.Companion.Conflict ->
                throw Exception("Логин уже занят")

            HttpStatusCode.Companion.InternalServerError -> {
                val bodyText = resp.bodyAsText()
                when {
                    "users_email_key" in bodyText ->
                        throw Exception("E-mail уже зарегистрирован")

                    else ->
                        throw Exception("Ошибка регистрации")
                }
            }

            HttpStatusCode.Companion.BadRequest,
            HttpStatusCode.Companion.Unauthorized ->
                throw Exception("Неверные данные для регистрации")

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun loginUser(login: String, password: String): String {
        val resp = client.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginDTO(login, password))
        }

        return when (resp.status) {
            HttpStatusCode.OK -> {
                val auth = resp.body<AuthResponse>()
                auth.token
            }

            HttpStatusCode.Companion.Unauthorized,
            HttpStatusCode.Companion.BadRequest ->
                throw Exception("Неправильный логин или пароль")

            else ->
                throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun getUserSettings(): UserDTO? {
        val resp = client.get("/settings/user") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> {
                val user = resp.body<UserDTO>()
                user
            }

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun changeEmail(newEmail: String): UserDTO? {
        val resp = client.post("/settings/email") {
            withAuthHeader()
            setBody(ChangeEmailDTO(newEmail))
        }

        return when (resp.status) {

            HttpStatusCode.OK -> {
                val user = resp.body<UserDTO>()
                user
            }

            else -> throw ResponseException(resp, resp.bodyAsText())

        }
    }

    override suspend fun changeLogin(newLogin: String): UserDTO? {
        val resp = client.post("/settings/login") {
            withAuthHeader()
            setBody(ChangeLoginDTO(newLogin))
        }

        return when (resp.status) {

            HttpStatusCode.OK -> {
                val user = resp.body<UserDTO>()
                user
            }
            else -> throw ResponseException(resp, resp.bodyAsText())

        }
    }

    override suspend fun changePassword(newPassword: String): UserDTO? {
        val resp = client.post("/settings/password") {
            withAuthHeader()
            setBody(ChangePasswordDTO(newPassword))
        }

        return when (resp.status) {

            HttpStatusCode.OK -> {
                val user = resp.body<UserDTO>()
                user
            }

            else -> throw ResponseException(resp, resp.bodyAsText())

        }
    }

    override suspend fun changeName(
        surname: String,
        name: String,
        lastName: String
    ): UserDTO? {
        val resp = client.post("/settings/name") {
            withAuthHeader()
            setBody(
                ChangeNameDTO(
                    surname = surname,
                    name = name,
                    lastName = lastName
                )
            )
        }

        return when (resp.status) {

            HttpStatusCode.OK -> {
                val user = resp.body<UserDTO>()
                user
            }
            else -> throw ResponseException(resp, resp.bodyAsText())

        }
    }
}