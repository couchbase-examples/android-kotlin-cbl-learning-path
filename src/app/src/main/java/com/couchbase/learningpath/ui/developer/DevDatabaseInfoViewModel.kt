@file:OptIn(ExperimentalCoroutinesApi::class)

package com.couchbase.learningpath.ui.developer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.couchbase.learningpath.data.KeyValueRepository
import com.couchbase.learningpath.data.audits.AuditRepository
import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.data.stockItem.StockItemRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepository
import com.couchbase.learningpath.services.AuthenticationService
import kotlinx.coroutines.ExperimentalCoroutinesApi

@kotlinx.serialization.ExperimentalSerializationApi
class DevDatabaseInfoViewModel(
    private val userProfileRepository: KeyValueRepository,
    private val warehouseRepository: WarehouseRepository,
    private val projectRepository: ProjectRepository,
    private val auditRepository: AuditRepository,
    private val stockItemRepository: StockItemRepository,
    authService: AuthenticationService
) : ViewModel() {

    private var currentUser = authService.getCurrentUser()
    var inventoryDatabaseName = mutableStateOf(userProfileRepository.inventoryDatabaseName())
    var inventoryDatabaseLocation =
        mutableStateOf(userProfileRepository.inventoryDatabaseLocation())
    var warehouseDatabaseName = warehouseRepository.warehouseDatabaseName
    var locationDatabaseLocation = warehouseRepository.warehouseDatabaseLocation
    var currentTeam = mutableStateOf("")
    var currentUsername = mutableStateOf("")
    var numberOfUserProfiles = mutableStateOf(0)
    var numberOfWarehouses = mutableStateOf(0)
    var numberOfStockItems = mutableStateOf(0)
    var numberOfProjects = mutableStateOf(0)
    var numberOfAudits = mutableStateOf(0)
    init {
        viewModelScope.launch {
            updateUserProfileInfo()
            updateUserProfileCount()
            updateWarehouseCount()
            updateStockItemCount()
            updateProjectCount()
            updateAuditCount()
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
            val count = userProfileRepository.count()  // <1>
            if (count > 0) {
                withContext(Dispatchers.Main) {
                    numberOfUserProfiles.value = count
                }
            }
        }
    }

    private suspend fun updateWarehouseCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val locationCount = warehouseRepository.warehouseCount()
            if (locationCount > 0) {
                withContext(Dispatchers.Main) {
                    numberOfWarehouses.value = locationCount
                }
            }
        }
    }

    private suspend fun updateStockItemCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val stockItemCount = stockItemRepository.count()
            if (stockItemCount > 0) {
                withContext(Dispatchers.Main) {
                    numberOfStockItems.value = stockItemCount
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

    private suspend fun updateAuditCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val auditCount = auditRepository.count()
            if (auditCount > 0) {
                withContext(Dispatchers.Main) {
                    numberOfAudits.value = auditCount
                }
            }
        }
    }
}