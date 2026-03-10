package net.plshark.vikunja.client

/** A [RuntimeException] indicating an issue calling a Vikunja API. */
class VikunjaException(
  val statusCode: Int?,
  message: String? = null,
  cause: Throwable? = null,
) : RuntimeException(message, cause)
