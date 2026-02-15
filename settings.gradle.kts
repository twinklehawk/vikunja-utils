rootProject.name = "vikunja-utils"

include(
  "app",
  "vikunja-client",
)

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}
