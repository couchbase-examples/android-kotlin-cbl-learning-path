@file:OptIn(ExperimentalCoroutinesApi::class)

package com.couchbase.learningpath.ui.project

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepository
import com.couchbase.learningpath.models.Warehouse
import kotlinx.coroutines.ExperimentalCoroutinesApi

@kotlinx.serialization.ExperimentalSerializationApi
class WarehouseSelectionViewModel(
    private val projectRepository: ProjectRepository,
    private val warehouseRepository: WarehouseRepository
) : ViewModel() {

    private var projectIdState = mutableStateOf<String?>(null)
    private var isLoading = mutableStateOf(false)

    val searchCity = mutableStateOf("")
    val searchState = mutableStateOf("")
    val warehousesState = mutableStateListOf<Warehouse>()
    var locationStatusMessage = mutableStateOf("No Warehouse Searched")

    val projectId: (String) -> Unit = {
        projectIdState.value = it
    }

    val onSearchCityChanged: (String) -> Unit = { newValue ->
        searchCity.value = newValue
    }

    val onSearchStateChanged: (String) -> Unit = { newValue ->
        searchState.value = newValue
    }

    val onSearch: () -> Unit = {
        viewModelScope.launch {  // <1>
            if (searchCity.value.length >= 2) {  // <2>
                isLoading.value = true
                val warehouses = warehouseRepository  // <3>
                    .getByCityState(searchCity.value, searchState.value) // <3>
                if (warehouses.isNotEmpty()) { // <4>
                    withContext(Dispatchers.Main) {
                        warehousesState.clear()
                        warehousesState.addAll(warehouses)
                        isLoading.value = false
                    }
                } else {  // <5>
                    withContext(Dispatchers.Main) {
                        warehousesState.clear()
                        locationStatusMessage.value = "No Locations Found"
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun onWarehouseSelected(warehouse: Warehouse) {
        projectIdState.value?.let {
            viewModelScope.launch(Dispatchers.IO) {
                projectRepository.updateProjectWarehouse(it, warehouse)
            }
        }
    }
}