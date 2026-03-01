package net.plshark.vikunja.client.internal.tasks

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import net.plshark.test.TestUtils.doBlocking
import net.plshark.vikunja.client.Something
import net.plshark.vikunja.client.VikunjaException
import net.plshark.vikunja.client.auth.ApiTokenProvider
import net.plshark.vikunja.client.models.ModelsTask
import net.plshark.vikunja.client.tasks.TasksClient
import okhttp3.Headers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TasksClientImplTest {
  private val server = MockWebServer()
  private lateinit var client: TasksClient

  @BeforeEach
  fun setup() {
    server.start()
    client =
      TasksClientImpl(
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
  fun `getTask succeeds`() =
    doBlocking {
      val task =
        ModelsTask(
          description = "task for testing",
          id = 0,
          projectId = 1,
          title = "test task",
        )
      server.enqueue(
        MockResponse(
          headers = Headers.headersOf("Content-Type", "application/json"),
          body = Json.encodeToString(task),
        ),
      )

      val retrievedTask = client.getTask(0)

      assertThat(retrievedTask).isEqualTo(task)
      val actualRequest = server.takeRequest()
      assertThat(actualRequest.method).isEqualTo("GET")
      assertThat(actualRequest.url.encodedPath).isEqualTo("/api/v1/tasks/0")
      assertThat(actualRequest.headers).contains(
        "Accept" to "application/json",
        "Authorization" to "Bearer test-token",
      )
    }

  @Test
  fun `getTask returns null if no task found`() =
    doBlocking {
      server.enqueue(
        MockResponse(
          code = HttpStatusCode.NotFound.value,
        ),
      )

      val retrievedTask = client.getTask(0)

      assertThat(retrievedTask).isNull()
    }

  @Test
  fun `getTask wraps http exceptions`() =
    doBlocking {
      server.enqueue(
        MockResponse(
          code = HttpStatusCode.InternalServerError.value,
        ),
      )

      assertThrows<VikunjaException> { client.getTask(0) }
    }

  @Test
  fun `createTask succeeds`() =
    doBlocking {
      val task =
        ModelsTask(
          description = "task for testing",
          projectId = 1,
          title = "test task",
        )
      server.enqueue(
        MockResponse(
          headers = Headers.headersOf("Content-Type", "application/json"),
          body = "{}",
        ),
      )

      val response = client.createTask(task)

      assertThat(response).isEqualTo(ModelsTask())
      val actualRequest = server.takeRequest()
      assertThat(actualRequest.method).isEqualTo("POST")
      assertThat(actualRequest.url.encodedPath).isEqualTo("/api/v1/projects/1/tasks")
      assertThat(actualRequest.headers).contains(
        "Accept" to "application/json",
        "Authorization" to "Bearer test-token",
        "Content-Type" to "application/json",
      )
      assertThat(actualRequest.body).isNotNull
      assertThat(Json.decodeFromString<ModelsTask>(actualRequest.body!!.utf8())).isEqualTo(task)
    }

  @Test
  fun `createTask rejects tasks with no project ID`() =
    doBlocking {
      val task = ModelsTask()

      assertThrows<IllegalArgumentException> { client.createTask(task) }
    }

  @Test
  fun `createTask wraps http exceptions`() =
    doBlocking {
      val task =
        ModelsTask(
          description = "task for testing",
          projectId = 1,
          title = "test task",
        )
      server.enqueue(
        MockResponse(
          code = HttpStatusCode.InternalServerError.value,
        ),
      )

      assertThrows<VikunjaException> { client.createTask(task) }
    }
}
