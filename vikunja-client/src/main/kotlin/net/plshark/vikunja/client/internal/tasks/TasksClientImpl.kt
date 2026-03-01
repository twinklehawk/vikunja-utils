package net.plshark.vikunja.client.internal.tasks

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import net.plshark.vikunja.client.VikunjaException
import net.plshark.vikunja.client.auth.BearerTokenProvider
import net.plshark.vikunja.client.internal.VikunjaHttpClientExt.executeRequest
import net.plshark.vikunja.client.internal.VikunjaHttpClientExt.executeRequestAndParseBody
import net.plshark.vikunja.client.models.ModelsTask
import net.plshark.vikunja.client.tasks.TasksClient

/** The default [TasksClient] implementation. */
class TasksClientImpl(
  private val httpClient: HttpClient,
  private val tokenProvider: BearerTokenProvider,
  private val host: String,
) : TasksClient {
  override suspend fun getTask(id: Int): ModelsTask? =
    try {
      httpClient.executeRequestAndParseBody {
        method = HttpMethod.Get
        url("${this@TasksClientImpl.host}/api/v1/tasks/$id")
        accept(ContentType.Application.Json)
        bearerAuth(tokenProvider.getBearerToken())
      }
    } catch (e: VikunjaException) {
      if (e.statusCode == HttpStatusCode.NotFound.value) {
        null
      } else {
        throw e
      }
    }

  override suspend fun createTask(task: ModelsTask): ModelsTask {
    requireNotNull(task.projectId) { "Project ID must be set" }

    return httpClient
      .executeRequest {
        method = HttpMethod.Post
        url("${this@TasksClientImpl.host}/api/v1/projects/${task.projectId}/tasks")
        accept(ContentType.Application.Json)
        bearerAuth(tokenProvider.getBearerToken())
        contentType(ContentType.Application.Json)
        setBody(task)
      }.body()
  }
}
