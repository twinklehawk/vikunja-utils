package net.plshark.vikunjataskcreator

import kotlinx.serialization.json.Json
import java.io.File

/**
 * Reads configuration from a JSON configuration file and environment variables.
 * Values from environment variables will overwrite values specified in the configuration file.
 */
class ConfigReader(
  private val configFilePath: String = DEFAULT_CONFIG_FILE_PATH,
  private val envVarMap: Map<String, String> = System.getenv(),
) {
  /** Reads configuration from the config file and environment variables. */
  fun readConfig(): VikunjaTaskCreatorConfig {
    val fileConfig = readConfigFile()
    return overwriteEnvVars(fileConfig)
  }

  private fun readConfigFile(): VikunjaTaskCreatorConfig {
    val config: VikunjaTaskCreatorConfig =
      File(configFilePath).bufferedReader(Charsets.UTF_8).use {
        Json.decodeFromString(it.readText())
      }
    return config
  }

  private fun overwriteEnvVars(config: VikunjaTaskCreatorConfig): VikunjaTaskCreatorConfig =
    config.copy(
      vikunja =
        config.vikunja.copy(
          apiKey = envVarMap[ENV_API_KEY] ?: config.vikunja.apiKey,
        ),
    )

  private companion object {
    const val DEFAULT_CONFIG_FILE_PATH = "config.json"
    const val ENV_API_KEY = "VIKUNJA_API_KEY"
  }
}
