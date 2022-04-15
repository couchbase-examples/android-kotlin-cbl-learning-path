package com.couchbase.learningpath.ui.project

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.models.Project
import com.couchbase.learningpath.services.AuthenticationService

class ProjectListViewModel(
    private val repository: ProjectRepository,
    private val authService: AuthenticationService,
) : ViewModel() {

    private var currentUser = authService.getCurrentUser()
    var isLoading = mutableStateOf(false)
    private lateinit var repositoryFlow: Flow<List<Project>>

    // create a flow to return the results dynamically as needed - more information on CoRoutine Flows can be found at
    // https://developer.android.com/kotlin/flow
    var projectsFlow: Flow<List<Project>> = flow {
        currentUser?.let { user ->
            isLoading.value = true
            repositoryFlow = repository.getDocuments(user.team)
            isLoading.value = false
            repositoryFlow.collect { items ->
                emit(items)
            }
        }
    }

    val deleteProject: (String) -> Boolean = { projectId: String ->
        var didDelete = false
        viewModelScope.launch(Dispatchers.IO) {
            didDelete = repository.delete(projectId)
        }
        didDelete
    }
}