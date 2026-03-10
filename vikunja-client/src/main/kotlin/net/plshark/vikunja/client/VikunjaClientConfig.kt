package net.plshark.vikunja.client

import kotlinx.serialization.Serializable

/** Configuration for accessing the Vikunja API. */
@Serializable
data class VikunjaClientConfig(
  val authenticationType: AuthenticationType,
  val apiKey: String? = null,
  val host: String,
  val http: HttpConfig = HttpConfig(),
) {
  /** Represents possible authentication types for accessing the Vikunja API. */
  enum class AuthenticationType {
    /** Use an API token to authenticate to the API. */
    ApiToken,
  }

  /** General HTTP configuration. */
  @Serializable
  data class HttpConfig(
    val connectTimeoutMs: Long = 5_000,
    val requestTimeoutMs: Long = 30_000,
    val socketTimeoutMs: Long = 30_000,
  )
}
