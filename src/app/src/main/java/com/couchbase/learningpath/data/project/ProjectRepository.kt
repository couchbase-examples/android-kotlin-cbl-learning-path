package com.couchbase.learningpath.data.project

import kotlinx.coroutines.flow.Flow

import com.couchbase.learningpath.data.Repository
import com.couchbase.learningpath.models.Location
import com.couchbase.learningpath.models.Project

interface ProjectRepository : Repository<Project> {
    suspend fun completeProject(projectId: String)
    fun getDocuments(team: String): Flow<List<Project>>
    suspend fun updateProjectLocation(projectId: String, location: Location)
}