plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation(libs.kotlin.jvm.plugin)
  implementation(libs.kotlinter.plugin)
  implementation(libs.detekt.plugin)
}

kotlin {
  jvmToolchain(21)
}
