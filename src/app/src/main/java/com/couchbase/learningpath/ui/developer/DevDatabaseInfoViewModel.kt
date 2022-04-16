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

class DevDatabaseInfoViewModel(
    private val userProfileRepository: KeyValueRepository,
    private val locationRepository: LocationRepository,
    private val projectRepository: ProjectRepository,
    authService: AuthenticationService
) : ViewModel() {

    private var currentUser = authService.getCurrentUser()
    var inventoryDatabaseName = mutableStateOf(userProfileRepository.inventoryDatabaseName())
    var inventoryDatabaseLocation =
        mutableStateOf(userProfileRepository.inventoryDatabaseLocation())
    var locationDatabaseName = locationRepository.locationDatabaseName
    var locationDatabaseLocation = locationRepository.locationDatabaseLocation
    var currentTeam = mutableStateOf("")
    var currentUsername = mutableStateOf("")
    var numberOfUserProfiles = mutableStateOf(0)
    var numberOfLocations = mutableStateOf(0)
    var numberOfProjects = mutableStateOf(0)

    init {
        viewModelScope.launch {
            updateUserProfileInfo()
            updateUserProfileCount()
            updateLocationCount()
            updateProjectCount()
        }
    }

    private suspend fun updateUserProfileInfo() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                currentUsername.value = currentUser.username
                currentTeam.value = currentUser.team

            }
        }
    }

    private suspend fun updateUserProfileCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = userProfileRepository.count()
            if (count > 0) {
                withContext(Dispatchers.Main) {
                    numberOfUserProfiles.value = count
                }
            }
        }
    }

    private suspend fun updateLocationCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val locationCount = locationRepository.locationCount()
            if (locationCount > 0) {
                withContext(Dispatchers.Main) {
                    numberOfLocations.value = locationCount
                }
            }
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
}