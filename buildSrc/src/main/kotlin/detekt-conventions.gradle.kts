plugins {
  kotlin("jvm")
  id("io.gitlab.arturbosch.detekt")
}

tasks.detekt {
  enabled = false
}
tasks.detektMain {
  buildUponDefaultConfig = true
  config.setFrom("../detekt-config.yaml",)
  exclude { it.file.path.contains("build/generated") }
}
tasks.detektTest {
  buildUponDefaultConfig = true
  config.setFrom("../detekt-config.yaml")
  exclude { it.file.path.contains("build/generated") }
}
tasks.check {
  dependsOn("detektMain", "detektTest")
}
