plugins {
  id("project-conventions")
  `java-library`
}

dependencies {
  api(libs.kotlinx.coroutines.core)

  testImplementation(libs.assertj)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
}
