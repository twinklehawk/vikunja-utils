package net.plshark.vikunja.client.internal

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
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
      val responseException = ResponseException(response, "response text")

      val e = responseException.toVikunjaException()

      assertThat(e.cause).isEqualTo(responseException)
      assertThat(e.statusCode).isEqualTo(HttpStatusCode.InternalServerError.value)
    }

  @Test
  fun `ioException toVikunjaException succeeds`() {
    val ioException = ConnectException()

    val e = ioException.toVikunjaException()

    assertThat(e.cause).isEqualTo(ioException)
  }
}
