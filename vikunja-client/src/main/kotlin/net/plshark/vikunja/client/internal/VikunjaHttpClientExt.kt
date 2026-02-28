package net.plshark.vikunja.client.internal

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import kotlinx.io.IOException
import net.plshark.vikunja.client.internal.HttpExceptionExt.toVikunjaException

/** Extension functions for HttpClient for Vikunja. */
internal object VikunjaHttpClientExt {
  /** Makes a request, parses the response body, and wraps any HTTP exceptions into a [net.plshark.vikunja.client.VikunjaException]. */
  internal suspend inline fun <reified T> HttpClient.executeRequestAndParseBody(
    block: HttpRequestBuilder.() -> Unit,
  ): T = executeRequest(block).body()

  /** Makes a request and wraps any HTTP exceptions into a [net.plshark.vikunja.client.VikunjaException]. */
  internal suspend inline fun HttpClient.executeRequest(block: HttpRequestBuilder.() -> Unit): HttpResponse {
    try {
      return request(block)
    } catch (e: ResponseException) {
      // response code exceptions
      throw e.toVikunjaException()
    } catch (e: IOException) {
      // timeout exceptions and socket exceptions
      throw e.toVikunjaException()
    }
  }
}
