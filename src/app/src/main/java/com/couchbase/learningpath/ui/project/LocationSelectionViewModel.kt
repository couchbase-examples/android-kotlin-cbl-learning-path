package com.couchbase.learningpath.ui.project

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.couchbase.learningpath.data.location.LocationRepository
import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.models.Location

class LocationSelectionViewModel(
    private val projectRepository: ProjectRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private var projectIdState = mutableStateOf<String?>(null)
    private var isLoading = mutableStateOf(false)

    val searchCity = mutableStateOf("")
    val searchCountry = mutableStateOf("")
    val locationsState = mutableStateListOf<Location>()
    var locationStatusMessage = mutableStateOf("No Location Searched")

    val projectId: (String) -> Unit = {
        projectIdState.value = it
    }

    val onSearchCityChanged: (String) -> Unit = { newValue ->
        searchCity.value = newValue
    }

    val onSearchCountryChanged: (String) -> Unit = { newValue ->
        searchCountry.value = newValue
    }

    val onSearch: () -> Unit = {
        viewModelScope.launch {
            if (searchCity.value.length >= 2) {
                isLoading.value = true
                val locations = locationRepository
                    .getByCityCountry(searchCity.value, searchCountry.value)
                if (locations.count() > 0) {
                    withContext(Dispatchers.Main) {
                        locationsState.clear()
                        locationsState.addAll(locations)
                        isLoading.value = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        locationsState.clear()
                        locationStatusMessage.value = "No Locations Found"
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun onLocationSelected(location: Location) {
        projectIdState.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                projectRepository.updateProjectLocation(it, location)
            }
        }
    }
}