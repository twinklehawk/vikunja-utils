package net.plshark.vikunja.client.tasks

import kotlinx.coroutines.flow.Flow
import net.plshark.vikunja.client.models.ModelsProject

/** A client for interacting with Vikunja projects. */
interface ProjectsClient {
  /** Gets all projects accessible by the current user. */
  fun getProjects(
    titleQuery: String? = null,
    fetchArchived: Boolean? = null,
    expand: String? = null,
  ): Flow<ModelsProject>

  /** Gets a single page of projects accessible by the current user. */
  suspend fun getProjectsPage(
    page: Int? = null,
    limit: Int? = null,
    titleQuery: String? = null,
    fetchArchived: Boolean? = null,
    expand: String? = null,
  ): List<ModelsProject>
}
