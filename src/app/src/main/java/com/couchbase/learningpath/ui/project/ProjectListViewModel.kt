@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.ui.project

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.models.Project
import com.couchbase.learningpath.services.AuthenticationService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoroutinesApi
class ProjectListViewModel(
    repository: ProjectRepository,
    authService: AuthenticationService,
) : ViewModel() {

    private var currentUser = authService.getCurrentUser()
    var isLoading = mutableStateOf(false)

    // create a flow to return the results dynamically as needed - more information on CoRoutine Flows can be found at
    // https://developer.android.com/kotlin/flow
    var repositoryFlow: Flow<List<Project>> = repository.getDocuments(currentUser.team)

    val deleteProject: (String) -> Boolean = { projectId: String ->
        var didDelete = false
        viewModelScope.launch(Dispatchers.IO) {
            didDelete = repository.delete(projectId)
        }
        didDelete
    }
}