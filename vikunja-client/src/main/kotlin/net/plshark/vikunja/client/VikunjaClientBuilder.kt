package net.plshark.vikunja.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.plshark.vikunja.client.auth.ApiTokenProvider
import net.plshark.vikunja.client.auth.BearerTokenProvider
import net.plshark.vikunja.client.internal.tasks.ProjectsClientImpl
import net.plshark.vikunja.client.internal.tasks.TasksClientImpl
import net.plshark.vikunja.client.tasks.ProjectsClient
import net.plshark.vikunja.client.tasks.TasksClient

/** A builder for creating Vikunja API client instances. */
class VikunjaClientBuilder(
  private val config: VikunjaClientConfig,
) {
  private var httpClient: HttpClient? = null
  private var tokenProvider: BearerTokenProvider? = null

  /** Builds a client for the Vikunja tasks API. */
  fun buildTasksClient(): TasksClient =
    TasksClientImpl(
      httpClient = getHttpClient(),
      tokenProvider = getAuthTokenProvider(),
      host = config.host,
    )

  /** Builds a client for the Vikunja projects API. */
  fun buildProjectsClient(): ProjectsClient =
    ProjectsClientImpl(
      httpClient = getHttpClient(),
      tokenProvider = getAuthTokenProvider(),
      host = config.host,
    )

  private fun getAuthTokenProvider(): BearerTokenProvider {
    if (tokenProvider == null) {
      tokenProvider =
        when (config.authenticationType) {
          VikunjaClientConfig.AuthenticationType.ApiToken -> createApiTokenProvider()
        }
    }
    return checkNotNull(tokenProvider)
  }

  private fun createApiTokenProvider(): BearerTokenProvider {
    checkNotNull(config.apiKey) { "API key cannot be null for ApiToken authentication type" }
    return ApiTokenProvider(config.apiKey)
  }

  private fun getHttpClient(): HttpClient {
    if (httpClient == null) {
      httpClient = createDefaultHttpClient()
    }
    return checkNotNull(httpClient)
  }

  internal fun createDefaultHttpClient(): HttpClient =
    HttpClient(CIO) {
      expectSuccess = true
      install(ContentNegotiation) {
        json(
          Json {
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = false
            encodeDefaults = true
            explicitNulls = false
            ignoreUnknownKeys = true
            isLenient = false
            prettyPrint = false
            useArrayPolymorphism = false
          },
        )
      }
      install(HttpTimeout) {
        connectTimeoutMillis = config.http.connectTimeoutMs
        requestTimeoutMillis = config.http.requestTimeoutMs
        socketTimeoutMillis = config.http.socketTimeoutMs
      }
    }
}
