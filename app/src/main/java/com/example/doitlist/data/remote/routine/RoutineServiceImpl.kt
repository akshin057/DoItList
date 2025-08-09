package com.example.doitlist.data.remote.routine

import com.example.doitlist.data.local.TokenStorage
import com.example.doitlist.data.remote.dto.IdResponse
import com.example.doitlist.data.remote.dto.RoutineDTO
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
import javax.inject.Inject

class RoutineServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : RoutineService {

    private fun HttpRequestBuilder.withAuthHeader() {
        tokenStorage.get()?.let { token ->
            println(token)
            header("Bearer-Authorisation", token)
        }
    }

    override suspend fun createRoutine(routineDTO: RoutineDTO): Long {
        val resp = client.post("/routines/create") {
            contentType(ContentType.Application.Json)
            setBody(routineDTO)
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> resp.body<IdResponse>().id

            HttpStatusCode.Conflict -> throw Exception("Такая рутинная задача уже есть")

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun updateRoutine(id: Long, routineDTO: RoutineDTO): Boolean {
        val resp = client.post("/routines/update/$id") {
            contentType(ContentType.Application.Json)
            setBody(routineDTO)
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true

            HttpStatusCode.Conflict -> false

            else -> throw ResponseException(resp, resp.bodyAsText())

        }
    }

    override suspend fun deleteRoutine(id: Long): Boolean {
        val resp = client.delete("/routines/delete/$id") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true

            HttpStatusCode.Conflict -> false

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun getRoutines(): List<RoutineDTO> {
        val resp = client.get("/routines/my") {
            withAuthHeader()
        }

        return when (resp.status) {

            HttpStatusCode.OK -> resp.body()

            else -> throw ResponseException(resp, resp.bodyAsText())
        }

    }
}