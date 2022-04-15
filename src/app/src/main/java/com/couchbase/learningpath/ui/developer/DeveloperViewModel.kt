package com.couchbase.learningpath.ui.developer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.couchbase.learningpath.data.KeyValueRepository
import com.couchbase.learningpath.data.location.LocationRepository
import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.services.AuthenticationService

class DeveloperViewModel(
    private val userProfileRepository: KeyValueRepository,
    private val locationRepository: LocationRepository,
    private val projectRepository: ProjectRepository,
    private val authService: AuthenticationService
) : ViewModel() {

    private var currentUser = authService.getCurrentUser()
    var currentTeam = mutableStateOf("")
    var currentUsername = mutableStateOf("")
    var numberOfUserProfiles = mutableStateOf(0)
    var numberOfLocations = mutableStateOf(0)
    var numberOfProjects = mutableStateOf(0)
    var toastMessage = mutableStateOf("")

    init {
        viewModelScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                currentUser?.let {
                    currentUsername.value = it.username
                    currentTeam.value = it.team
                }
            }

            val locationCount = locationRepository.locationCount()
            if (locationCount > 0) {
                withContext(Dispatchers.Main){
                    numberOfLocations.value = locationCount
                }
            }

            val count = userProfileRepository.count()
            if (count > 0){
                withContext(Dispatchers.Main){
                    numberOfUserProfiles.value = count
                }
            }
            updateProjectCount()
        }
    }

    private suspend fun updateProjectCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val projectCount = projectRepository.count()
            if (projectCount > 0) {
                withContext(Dispatchers.Main) {
                    numberOfProjects.value = projectCount
                }
            }
        }
    }

    val onLoadSampleData: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO){
            projectRepository.loadSampleData()
            updateProjectCount()
            toastMessage.value = "Load Sample Data Completed"
        }
    }

    val clearToastMessage: () -> Unit = {
        toastMessage.value = ""
    }
}