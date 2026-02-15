import org.jmailen.gradle.kotlinter.tasks.FormatTask
import org.jmailen.gradle.kotlinter.tasks.LintTask

plugins {
  id("project-conventions")
  `java-library`
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.openapi.generator)
}

dependencies {
  api(libs.kotlinx.coroutines.core)

  implementation(libs.kotlin.reflect)
  implementation(libs.kotlin.stdlib)
  implementation(libs.slf4j.api)

  // TODO
  implementation(libs.ktor.client.cio)
  implementation(libs.ktor.client.core)
  implementation(libs.ktor.client.contentNegotiation)
  implementation(libs.ktor.serialization.json)

  testImplementation(libs.assertj)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.mockk)
  testImplementation(libs.mockwebserver)

  testRuntimeOnly(libs.junit.launcher)
  testRuntimeOnly(libs.logback)
}

openApiGenerate {
  generateApiDocumentation = false
  generateApiTests = false
  generateModelDocumentation = false
  generateModelTests = false
  generatorName = "kotlin"
  inputSpec = "$projectDir/src/main/resources/openapi.yaml"
  library = "jvm-ktor"
  outputDir = "$projectDir/build/generated/openapi"
  configOptions = mapOf(
    "omitGradleWrapper" to "true",
    "packageName" to "net.plshark.vikunja.client",
    "serializationLibrary" to "kotlinx_serialization",
  )
  globalProperties = mapOf(
    "apis" to "false",
    "models" to "",
  )
}
tasks.compileKotlin {
  dependsOn("openApiGenerate")
}
kotlin {
  sourceSets.main {
    kotlin.srcDir("build/generated/openapi/src/main/kotlin")
  }
}

tasks.withType<LintTask> {
  dependsOn("openApiGenerate")
  exclude { it.file.path.contains("build/generated") }
}
tasks.withType<FormatTask> {
  dependsOn("openApiGenerate")
  exclude { it.file.path.contains("build/generated") }
}
