package com.example.doitlist.data.remote.project

import com.example.doitlist.data.local.TokenStorage
import com.example.doitlist.data.remote.dto.IdResponse
import com.example.doitlist.data.remote.dto.ProjectDTO
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

class ProjectServiceImpl @Inject constructor(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage
) : ProjectService {

    private fun HttpRequestBuilder.withAuthHeader() {
        tokenStorage.get()?.let { token ->
            println(token)
            header("Bearer-Authorisation", token)
        }
    }

    override suspend fun createProject(projectDTO: ProjectDTO): Long {
        val resp = client.post("/projects/create") {
            contentType(ContentType.Application.Json)
            setBody(projectDTO)
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> resp.body<IdResponse>().id

            HttpStatusCode.Companion.Conflict ->
                throw Exception("Такой проект у вас уже есть")

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun updateProject(id: Long, projectDTO: ProjectDTO): Boolean {
        val resp = client.post("/projects/update/$id") {
            contentType(ContentType.Application.Json)
            setBody(projectDTO)
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true

            HttpStatusCode.Companion.Conflict ->
                false

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun getProjects(): List<ProjectDTO> {
        val resp = client.get("/projects/my") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> resp.body()
            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }

    override suspend fun deleteProjects(id: Long): Boolean {
        val resp = client.delete("/projects/$id") {
            withAuthHeader()
        }

        return when (resp.status) {
            HttpStatusCode.OK -> true

            HttpStatusCode.Conflict -> false

            else -> throw ResponseException(resp, resp.bodyAsText())
        }
    }
}