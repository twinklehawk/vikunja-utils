package net.plshark.vikunja.client.tasks

import net.plshark.vikunja.client.models.ModelsTask

/** A client for interacting with Vikunja tasks. */
interface TasksClient {
  /** Retrieves a task by ID. */
  suspend fun getTask(id: Int): ModelsTask?

  /**
   * Inserts a task into a project.
   *
   * The task project ID must be set.
   */
  suspend fun createTask(task: ModelsTask): ModelsTask
}
