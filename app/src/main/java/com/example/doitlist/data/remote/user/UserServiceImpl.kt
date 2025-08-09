package com.example.doitlist.data.remote.user

import com.example.doitlist.data.remote.dto.AuthResponse
import com.example.doitlist.data.remote.dto.LoginDTO
import com.example.doitlist.data.remote.dto.UserDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import javax.inject.Inject

class UserServiceImpl @Inject constructor(
    private val client: HttpClient
) : UserService {

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


}