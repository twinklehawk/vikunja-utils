package net.plshark.vikunja.client.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ApiTokenProviderTest {
  @Test
  fun `getBearerToken returns the API token`() {
    val provider = ApiTokenProvider("test-token")

    assertThat(provider.getBearerToken()).isEqualTo("test-token")
  }
}
