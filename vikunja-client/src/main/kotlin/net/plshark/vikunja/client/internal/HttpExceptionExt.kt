package net.plshark.vikunja.client.internal

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.io.IOException
import net.plshark.vikunja.client.VikunjaException

/** Extension functions for ktor client exceptions. */
internal object HttpExceptionExt {
  /** Creates a [net.plshark.vikunja.client.VikunjaException] for a [io.ktor.client.plugins.ResponseException]. */
  suspend fun ResponseException.toVikunjaException() =
    VikunjaException(
      statusCode = response.status.value,
      message = response.bodyAsText(),
      cause = this,
    )

  /** Creates a [VikunjaException] for an [kotlinx.io.IOException]. */
  fun IOException.toVikunjaException() =
    VikunjaException(
      statusCode = null,
      message = message,
      cause = this,
    )
}
