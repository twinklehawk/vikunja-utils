rootProject.name = "vikunja-utils"

include(
  "test-utils",
  "vikunja-client",
  "vikunja-task-creator",
)

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}
