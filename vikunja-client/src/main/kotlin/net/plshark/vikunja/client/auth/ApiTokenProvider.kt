package net.plshark.vikunja.client.auth

/** A [BearerTokenProvider] that provides a static API token. */
class ApiTokenProvider(
  private val apiToken: String,
) : BearerTokenProvider {
  override fun getBearerToken(): String = apiToken
}
