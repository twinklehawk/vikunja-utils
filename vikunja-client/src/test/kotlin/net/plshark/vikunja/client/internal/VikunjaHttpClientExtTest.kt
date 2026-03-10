package net.plshark.vikunja.client.internal

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import net.plshark.test.TestUtils.doBlocking
import net.plshark.vikunja.client.VikunjaException
import net.plshark.vikunja.client.internal.VikunjaHttpClientExt.executeRequest
import net.plshark.vikunja.client.internal.VikunjaHttpClientExt.executeRequestAndParseBody
import okhttp3.Headers
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class VikunjaHttpClientExtTest {
  private val server = MockWebServer()
  private lateinit var mockHost: String
  private lateinit var client: HttpClient

  @BeforeEach
  fun setup() {
    server.start()
    mockHost = server.url("").toString().removeSuffix("/")
    client =
      HttpClient(CIO) {
        expectSuccess = true
        install(ContentNegotiation) {
          json(Json)
        }
        install(HttpTimeout) {
          requestTimeoutMillis = 1000
        }
      }
  }

  @AfterEach
  fun cleanup() {
    server.close()
  }

  @Test
  fun `executeRequest returns the response`() =
    doBlocking {
      server.enqueue(
        MockResponse(),
      )

      val result =
        client.executeRequest {
          method = HttpMethod.Get
          url(mockHost)
        }

      assertThat(result.status).isEqualTo(HttpStatusCode.OK)
    }

  @Test
  fun `executeRequest wraps server exceptions`() =
    doBlocking {
      server.enqueue(
        MockResponse(
          code = HttpStatusCode.InternalServerError.value,
        ),
      )

      val e =
        assertThrows<VikunjaException> {
          client.executeRequest {
            method = HttpMethod.Get
            url(mockHost)
          }
        }
      assertThat(e.statusCode).isEqualTo(HttpStatusCode.InternalServerError.value)
    }

  @Test
  fun `executeRequest wraps client exceptions`() =
    doBlocking {
      server.enqueue(
        MockResponse(
          code = HttpStatusCode.BadRequest.value,
        ),
      )

      val e =
        assertThrows<VikunjaException> {
          client.executeRequest {
            method = HttpMethod.Get
            url(mockHost)
          }
        }
      assertThat(e.statusCode).isEqualTo(HttpStatusCode.BadRequest.value)
    }

  @Test
  fun `executeRequest wraps socket exceptions`() =
    doBlocking {
      val e =
        assertThrows<VikunjaException> {
          client.executeRequest {
            method = HttpMethod.Get
            url(mockHost)
          }
        }
      assertThat(e.statusCode).isNull()
    }

  @Test
  fun `executeRequestAndParseBody parses the response body`() =
    doBlocking {
      server.enqueue(
        MockResponse(
          body = "{\"key\": \"value\"}",
          headers = Headers.headersOf("Content-Type", "application/json"),
        ),
      )

      val result =
        client.executeRequestAndParseBody<DummyBody> {
          method = HttpMethod.Get
          url(mockHost)
        }

      assertThat(result.key).isEqualTo("value")
    }

  @Serializable
  private data class DummyBody(
    val key: String,
  )
}
