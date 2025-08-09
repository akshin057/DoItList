package com.example.doitlist.data.remote.routinelogs

import androidx.annotation.IdRes
import com.example.doitlist.data.local.TokenStorage
import com.example.doitlist.data.remote.dto.IdResponse
import com.example.doitlist.data.remote.dto.RoutineLogDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.time.LocalDate
import javax.inject.Inject

class RoutineLogServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : RoutineLogService {

    private fun HttpRequestBuilder.withAuthHeader() {
        tokenStorage.get()?.let { token ->
            println(token)
            header("Bearer-Authorisation", token)
        }
    }

    override suspend fun createRoutineLogs(routineLogDTO: RoutineLogDTO): Long {
        println(routineLogDTO)
        val resp = client.post("/routineLogs/create") {
            contentType(ContentType.Application.Json)
            setBody(routineLogDTO)
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> resp.body<IdResponse>().id

            HttpStatusCode.Conflict -> throw Exception("Что-то пошло не так")

            else -> throw ResponseException(resp, resp.bodyAsText())
        }

    }

    override suspend fun deleteRoutineLog(id: Long): Boolean {
        val resp = client.delete("/routineLogs/delete/$id") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true

            HttpStatusCode.Conflict -> false

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun getRoutineLogs(): List<RoutineLogDTO> {
        val resp = client.get("/routineLogs/my") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> resp.body()

            HttpStatusCode.Conflict -> throw Exception("Что-то не так")

            else -> throw ResponseException(resp, resp.bodyAsText())
        }

    }

    override suspend fun getCompletedToday(): List<Long> {
        val resp = client.get("routineLogs/completed/today") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> resp.body()

            else -> throw ResponseException(resp, resp.bodyAsText())
        }

    }
}