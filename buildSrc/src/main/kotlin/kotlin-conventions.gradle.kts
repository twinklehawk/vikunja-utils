plugins {
  kotlin("jvm")
}

kotlin {
  jvmToolchain(21)
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
  implementation(platform(libs.findLibrary("kotlin-bom").get()))
  implementation(platform(libs.findLibrary("kotlinx-coroutines-bom").get()))
  implementation(platform(libs.findLibrary("ktor-bom").get()))
  implementation(platform(libs.findLibrary("junit-bom").get()))
}
