plugins {
  id("project-conventions")
  alias(libs.plugins.kotlin.serialization)
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

val dependenciesDir = layout.buildDirectory.dir("libs/dependencies")
val copyDependencies by tasks.registering(Copy::class) {
  from(configurations.runtimeClasspath)
  into(dependenciesDir)
}
tasks.jar {
  dependsOn(copyDependencies)
  manifest {
    attributes(
      "Main-Class" to "net.plshark.vikunjataskcreator.VikunjaTaskCreatorKt",
      "Class-Path" to configurations.runtimeClasspath.get().joinToString(" ") { "dependencies/${it.name}" }
    )
  }
}
