package net.plshark.vikunja.client.internal.tasks

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import net.plshark.test.TestUtils.doBlocking
import net.plshark.vikunja.client.Something
import net.plshark.vikunja.client.auth.ApiTokenProvider
import net.plshark.vikunja.client.models.ModelsProject
import net.plshark.vikunja.client.tasks.ProjectsClient
import okhttp3.Headers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProjectsClientImplTest {
  private val server = MockWebServer()
  private lateinit var client: ProjectsClient

  @BeforeEach
  fun setup() {
    server.start()
    client =
      ProjectsClientImpl(
        httpClient = Something.createDefaultHttpClient(),
        tokenProvider = ApiTokenProvider("test-token"),
        host = server.url("").toString().removeSuffix("/"),
      )
  }

  @AfterEach
  fun cleanup() {
    server.close()
  }

  @Test
  fun `getProjects succeeds with single page`() =
    doBlocking {
      val projects =
        listOf(
          ModelsProject(id = 1, title = "project1"),
          ModelsProject(id = 2, title = "project2"),
        )
      server.enqueue(
        MockResponse(
          headers =
            Headers.headersOf(
              "Content-Type",
              "application/json",
              ProjectsClientImpl.PAGINATION_HEADER,
              "1",
            ),
          body = Json.encodeToString(projects),
        ),
      )

      val result = client.getProjects().toList()

      assertThat(result).isEqualTo(projects)
      val actualRequest = server.takeRequest()
      assertThat(actualRequest.method).isEqualTo("GET")
      assertThat(actualRequest.url.encodedPath).isEqualTo("/api/v1/projects")
      assertThat(actualRequest.url.queryParameter("page")).isEqualTo("1")
      assertThat(actualRequest.headers).contains(
        "Accept" to "application/json",
        "Authorization" to "Bearer test-token",
      )
    }

  @Test
  fun `getProjects succeeds with multiple pages`() =
    doBlocking {
      val page1 =
        listOf(
          ModelsProject(id = 1, title = "project1"),
          ModelsProject(id = 2, title = "project2"),
        )
      val page2 =
        listOf(
          ModelsProject(id = 3, title = "project3"),
          ModelsProject(id = 4, title = "project4"),
        )
      server.enqueue(
        MockResponse(
          headers =
            Headers.headersOf(
              "Content-Type",
              "application/json",
              ProjectsClientImpl.PAGINATION_HEADER,
              "2",
            ),
          body = Json.encodeToString(page1),
        ),
      )
      server.enqueue(
        MockResponse(
          headers =
            Headers.headersOf(
              "Content-Type",
              "application/json",
              ProjectsClientImpl.PAGINATION_HEADER,
              "2",
            ),
          body = Json.encodeToString(page2),
        ),
      )

      val result = client.getProjects().toList()

      assertThat(result).isEqualTo(page1 + page2)
      val request1 = server.takeRequest()
      assertThat(request1.url.queryParameter("page")).isEqualTo("1")
      val request2 = server.takeRequest()
      assertThat(request2.url.queryParameter("page")).isEqualTo("2")
    }

  @Test
  fun `getProjects sets query parameters if not null`() =
    doBlocking {
      server.enqueue(
        MockResponse(
          headers =
            Headers.headersOf(
              "Content-Type",
              "application/json",
              ProjectsClientImpl.PAGINATION_HEADER,
              "1",
            ),
          body = "[]",
        ),
      )

      client
        .getProjects(
          titleQuery = "title",
          fetchArchived = true,
          expand = "Permissions",
        ).collect()

      val actualRequest = server.takeRequest()
      assertThat(actualRequest.url.queryParameter("page")).isEqualTo("1")
      assertThat(actualRequest.url.queryParameter("s")).isEqualTo("title")
      assertThat(actualRequest.url.queryParameter("is_archived")).isEqualTo("true")
      assertThat(actualRequest.url.queryParameter("expand")).isEqualTo("Permissions")
    }

  @Test
  fun `getProjectsPage succeeds`() =
    doBlocking {
      val projects =
        listOf(
          ModelsProject(id = 1, title = "project1"),
          ModelsProject(id = 2, title = "project2"),
        )
      server.enqueue(
        MockResponse(
          headers =
            Headers.headersOf(
              "Content-Type",
              "application/json",
              ProjectsClientImpl.PAGINATION_HEADER,
              "2",
            ),
          body = Json.encodeToString(projects),
        ),
      )

      val result = client.getProjectsPage()

      assertThat(result).isEqualTo(projects)
      val actualRequest = server.takeRequest()
      assertThat(actualRequest.method).isEqualTo("GET")
      assertThat(actualRequest.url.encodedPath).isEqualTo("/api/v1/projects")
      assertThat(actualRequest.headers).contains(
        "Accept" to "application/json",
        "Authorization" to "Bearer test-token",
      )
    }

  @Test
  fun `getProjectsPage sets query parameters if not null`() =
    doBlocking {
      server.enqueue(
        MockResponse(
          headers =
            Headers.headersOf(
              "Content-Type",
              "application/json",
              ProjectsClientImpl.PAGINATION_HEADER,
              "1",
            ),
          body = "[]",
        ),
      )

      client.getProjectsPage(
        titleQuery = "title",
        fetchArchived = true,
        expand = "Permissions",
        page = 2,
        limit = 5,
      )

      val actualRequest = server.takeRequest()
      assertThat(actualRequest.url.queryParameter("page")).isEqualTo("2")
      assertThat(actualRequest.url.queryParameter("per_page")).isEqualTo("5")
      assertThat(actualRequest.url.queryParameter("s")).isEqualTo("title")
      assertThat(actualRequest.url.queryParameter("is_archived")).isEqualTo("true")
      assertThat(actualRequest.url.queryParameter("expand")).isEqualTo("Permissions")
    }
}
