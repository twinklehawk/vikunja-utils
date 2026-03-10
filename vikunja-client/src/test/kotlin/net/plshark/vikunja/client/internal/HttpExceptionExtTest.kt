package net.plshark.vikunja.client.internal

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.io.Buffer
import kotlinx.io.Source
import net.plshark.test.TestUtils
import net.plshark.vikunja.client.internal.HttpExceptionExt.toVikunjaException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.ConnectException

class HttpExceptionExtTest {
  @Test
  fun `responseException toVikunjaException succeeds`() =
    TestUtils.doBlocking {
      val response = mockk<HttpResponse>()
      every { response.status.value } returns HttpStatusCode.InternalServerError.value
      every { response.headers } returns headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
      val buffer = Buffer()
      buffer.write("test message".toByteArray())
      coEvery { response.call.body<Source>() } returns buffer
      val responseException = ResponseException(response, "response text")

      val e = responseException.toVikunjaException()

      assertThat(e.cause).isEqualTo(responseException)
      assertThat(e.statusCode).isEqualTo(HttpStatusCode.InternalServerError.value)
      assertThat(e.message).isEqualTo("test message")
    }

  @Test
  fun `ioException toVikunjaException succeeds`() {
    val ioException = ConnectException()

    val e = ioException.toVikunjaException()

    assertThat(e.cause).isEqualTo(ioException)
  }
}
