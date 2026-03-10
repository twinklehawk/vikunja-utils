package net.plshark.vikunjataskcreator

import kotlinx.serialization.Serializable
import net.plshark.vikunja.client.VikunjaClientConfig

/** Vikunja task creator configuration. */
@Serializable
data class VikunjaTaskCreatorConfig(
  val vikunja: VikunjaClientConfig,
  val tasks: List<TaskConfig>,
) {
  /** Configuration for a task to create. */
  @Serializable
  data class TaskConfig(
    val title: String,
    val project: String,
    val description: String? = null,
  )
}
