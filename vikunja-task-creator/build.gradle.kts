plugins {
  id("project-conventions")
  alias(libs.plugins.kotlin.serialization)
  application
}

dependencies {
  implementation(project(":vikunja-client"))
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlin.stdlib)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.slf4j.api)
  runtimeOnly(libs.logback)

  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.assertj)
  testImplementation(libs.mockwebserver)
  testImplementation(libs.kotlinx.coroutines.test)
  testRuntimeOnly(libs.junit.launcher)
}

application {
  mainClass = "net.plshark.vikunjataskcreator.VikunjaTaskCreatorKt"
}
