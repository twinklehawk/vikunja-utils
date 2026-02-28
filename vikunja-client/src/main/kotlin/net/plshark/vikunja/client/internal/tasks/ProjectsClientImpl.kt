package net.plshark.vikunja.client.internal.tasks

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.plshark.vikunja.client.auth.BearerTokenProvider
import net.plshark.vikunja.client.internal.VikunjaHttpClientExt.executeRequest
import net.plshark.vikunja.client.models.ModelsProject
import net.plshark.vikunja.client.tasks.ProjectsClient

/** The default [ProjectsClient] implementation. */
class ProjectsClientImpl(
  private val httpClient: HttpClient,
  private val tokenProvider: BearerTokenProvider,
  private val host: String,
) : ProjectsClient {
  override fun getProjects(
    titleQuery: String?,
    fetchArchived: Boolean?,
    expand: String?,
  ): Flow<ModelsProject> =
    flow {
      var currentPage = 0
      var hasMorePages: Boolean
      do {
        currentPage += 1
        val response =
          getProjectsPageInternal(
            page = currentPage,
            titleQuery = titleQuery,
            fetchArchived = fetchArchived,
            expand = expand,
          )
        val totalPages = response.headers[PAGINATION_HEADER]?.toInt() ?: 1
        val body = response.body<List<ModelsProject>>()
        emitAll(body.asFlow())
        hasMorePages = totalPages > currentPage
      } while (hasMorePages)
    }

  override suspend fun getProjectsPage(
    page: Int?,
    limit: Int?,
    titleQuery: String?,
    fetchArchived: Boolean?,
    expand: String?,
  ): List<ModelsProject> =
    getProjectsPageInternal(
      page = page,
      limit = limit,
      titleQuery = titleQuery,
      fetchArchived = fetchArchived,
      expand = expand,
    ).body()

  private suspend fun getProjectsPageInternal(
    page: Int? = null,
    limit: Int? = null,
    titleQuery: String? = null,
    fetchArchived: Boolean? = null,
    expand: String? = null,
  ): HttpResponse =
    httpClient.executeRequest {
      method = HttpMethod.Get
      url("${this@ProjectsClientImpl.host}/api/v1/projects")
      url {
        page?.also { parameters.append("page", page.toString()) }
        limit?.also { parameters.append("per_page", limit.toString()) }
        titleQuery?.also { parameters.append("s", titleQuery) }
        fetchArchived?.also { parameters.append("is_archived", fetchArchived.toString()) }
        expand?.also { parameters.append("expand", expand) }
      }
      accept(ContentType.Application.Json)
      bearerAuth(tokenProvider.getBearerToken())
    }

  internal companion object {
    const val PAGINATION_HEADER = "x-pagination-total-pages"
  }
}
