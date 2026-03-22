package net.plshark.vikunjataskcreator

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.plshark.vikunja.client.VikunjaClientBuilder
import net.plshark.vikunja.client.models.ModelsTask
import net.plshark.vikunja.client.tasks.ProjectsClient
import net.plshark.vikunja.client.tasks.TasksClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/** Creates Vikunja tasks. */
class VikunjaTaskCreator(
  private val config: VikunjaTaskCreatorConfig,
  private val projectsClient: ProjectsClient,
  private val tasksClient: TasksClient,
) {
  /** Creates all Vikunja tasks according to the configuration */
  suspend fun createTasks() {
    logger.info("Creating ${config.tasks.size} tasks")
    for (taskConfig in config.tasks) {
      val createdTask = createTask(taskConfig)
      logger.info("Created task ${createdTask.id}")
    }
    logger.info("Created ${config.tasks.size} tasks")
  }

  private suspend fun createTask(taskConfig: VikunjaTaskCreatorConfig.TaskConfig): ModelsTask {
    val projectId = getProjectIdForName(taskConfig.project)
    val task =
      ModelsTask(
        title = taskConfig.title,
        projectId = projectId,
        description = taskConfig.description,
      )
    return tasksClient.createTask(task)
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

  private companion object {
    val logger: Logger = LoggerFactory.getLogger(VikunjaTaskCreator::class.java)
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
