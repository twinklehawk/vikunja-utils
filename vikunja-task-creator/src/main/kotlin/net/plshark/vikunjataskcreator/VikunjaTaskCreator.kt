package net.plshark.vikunjataskcreator

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.plshark.vikunja.client.VikunjaClientBuilder
import net.plshark.vikunja.client.models.ModelsTask
import net.plshark.vikunja.client.tasks.ProjectsClient
import net.plshark.vikunja.client.tasks.TasksClient

/** Creates Vikunja tasks. */
class VikunjaTaskCreator(
  private val config: VikunjaTaskCreatorConfig,
  private val projectsClient: ProjectsClient,
  private val tasksClient: TasksClient,
) {
  /** Creates all Vikunja tasks according to the configuration */
  suspend fun createTasks() {
    for (taskConfig in config.tasks) {
      createTask(taskConfig)
    }
  }

  private suspend fun createTask(taskConfig: VikunjaTaskCreatorConfig.TaskConfig) {
    val projectId = getProjectIdForName(taskConfig.project)
    val task =
      ModelsTask(
        title = taskConfig.title,
        projectId = projectId,
        description = taskConfig.description,
      )
    tasksClient.createTask(task)
  }

  private suspend fun getProjectIdForName(projectName: String): Int {
    val projectId =
      projectsClient
        .getProjects(titleQuery = projectName)
        .first { it.title == projectName }
        .id
    checkNotNull(projectId) { "Project ID null for project $projectName" }
    return projectId
  }
}

/** Application entry point. */
fun main() {
  val config = ConfigReader().readConfig()

  val builder = VikunjaClientBuilder(config.vikunja)
  val tasksClient = builder.buildTasksClient()
  val projectsClient = builder.buildProjectsClient()

  runBlocking {
    VikunjaTaskCreator(config, projectsClient, tasksClient).createTasks()
  }
}
