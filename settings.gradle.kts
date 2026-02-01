rootProject.name = "vikunja-task-scheduler"

include(
  "app",
  "vikunja-client",
)

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}
