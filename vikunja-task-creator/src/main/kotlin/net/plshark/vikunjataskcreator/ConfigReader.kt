package net.plshark.vikunjataskcreator

import kotlinx.serialization.json.Json
import java.io.File

/** Reads configuration from a JSON config file. */
class ConfigReader {
  /** Reads configuration from the specified JSON config file. */
  fun readConfig(path: String = "config.json"): VikunjaTaskCreatorConfig {
    val config: VikunjaTaskCreatorConfig =
      File(path).bufferedReader(Charsets.UTF_8).use {
        Json.decodeFromString(it.readText())
      }
    return config
  }
}
