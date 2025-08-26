package com.example.doitlist.data.remote.task

import com.example.doitlist.data.local.TokenStorage
import com.example.doitlist.data.remote.dto.IdResponse
import com.example.doitlist.data.remote.dto.TaskDTO
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
import io.ktor.http.headers
import javax.inject.Inject

class TaskServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : TaskService {

    private fun HttpRequestBuilder.withAuthHeader() {
        tokenStorage.get()?.let { token ->
            println(token)
            header("Bearer-Authorisation", token)
        }
    }

    override suspend fun createTask(taskDTO: TaskDTO): Long {
        val resp = client.post("/tasks/create") {
            contentType(ContentType.Application.Json)
            setBody(taskDTO)
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> resp.body<IdResponse>().id

            HttpStatusCode.Companion.Conflict ->
                throw Exception("Такая задача у вас уже есть")

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun updateTask(id: Long, taskDTO: TaskDTO): Boolean {

        val resp = client.post("/tasks/update/$id") {
            contentType(ContentType.Application.Json)
            setBody(taskDTO)
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true

            HttpStatusCode.Companion.Conflict -> false

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun completeTask(id: Long): Boolean {
        val resp = client.post("/tasks/complete/$id") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true

            HttpStatusCode.Companion.Conflict -> false

            else -> throw ResponseException(resp, resp.bodyAsText())
        }

    }

    override suspend fun deleteTask(id: Long): Boolean {
        val resp = client.delete("/tasks/delete/$id") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true

            HttpStatusCode.Companion.Conflict -> false

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun getTasks(): List<TaskDTO> {
        val resp = client.get("/tasks/my") {
            withAuthHeader()
        }
        return when (resp.status) {
            HttpStatusCode.OK -> resp.body()

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun deleteTasksByProject(projectId: Long): Boolean {
        val resp = client.delete("/tasks/delete/project/$projectId") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true
            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun rescheduleTask(taskId: Long): Boolean {
        val resp = client.post("/tasks/reschedule/$taskId") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true
            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }
}