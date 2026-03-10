package net.plshark.vikunja.client.auth

/** Provides bearer tokens for authentication to Vikunja APIs. */
interface BearerTokenProvider {
  /** Gets a valid bearer token to use in an authorization header.  */
  fun getBearerToken(): String
}
