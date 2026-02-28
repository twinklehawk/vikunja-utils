plugins {
  id("project-conventions")
}

dependencies {
  implementation(project(":vikunja-client"))
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlin.stdlib)
  implementation(libs.slf4j.api)
  runtimeOnly(libs.logback)

  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.assertj)
  testImplementation(libs.mockwebserver)
  testImplementation(libs.kotlinx.coroutines.test)
  testRuntimeOnly(libs.junit.launcher)
}
