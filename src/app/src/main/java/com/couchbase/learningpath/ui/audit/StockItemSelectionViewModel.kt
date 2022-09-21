package com.couchbase.learningpath.ui.audit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.learningpath.data.audits.AuditRepository
import com.couchbase.learningpath.data.stockItem.StockItemRepository
import com.couchbase.learningpath.models.StockItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@kotlinx.serialization.ExperimentalSerializationApi
class StockItemSelectionViewModel(
    private val auditRepository: AuditRepository,
    private val stockItemRepository: StockItemRepository
) : ViewModel() {
    private var auditIdState = mutableStateOf<String?>(null)
    private var projectIdState = mutableStateOf<String?>(null)
    private var isLoading = mutableStateOf(false)

    var navigateUp: () -> Unit =  { }

    val searchName = mutableStateOf("")
    val searchDescription = mutableStateOf("")
    val stockItemsState = mutableStateListOf<StockItem>()
    var statusMessage = mutableStateOf("No stock item searched")

    val projectId: (String) -> Unit = {
        projectIdState.value = it
    }

    val auditId: (String) -> Unit = {
        auditIdState.value = it
    }

    val onSearchNameChanged: (String) -> Unit = { newValue ->
        searchName.value = newValue
    }

    val onSearchDescriptionChanged: (String) -> Unit = { newValue ->
        searchDescription.value = newValue
    }

    val onSearch: () -> Unit = {
        viewModelScope.launch {  // <1>
            if (searchName.value.length >= 2) {  // <2>
                isLoading.value = true
                val foundItems = stockItemRepository
                    .getByNameDescription(searchName.value, searchDescription.value) // <3>
                if (foundItems.isNotEmpty()) { // <4>
                    withContext(Dispatchers.Main) {
                        stockItemsState.clear()
                        stockItemsState.addAll(foundItems)
                        isLoading.value = false
                    }
                } else {  // <5>
                    withContext(Dispatchers.Main) {
                        stockItemsState.clear()
                        statusMessage.value = "No stock items Found"
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun onStockItemSelected(stockItem: StockItem) {
        projectIdState.value?.let { projectId ->
            auditIdState.value?.let { auditId ->
                viewModelScope.launch(Dispatchers.IO) {
                    auditRepository.updateAuditStockItem(projectId, auditId, stockItem)
                    withContext(Dispatchers.Main) {
                        navigateUp()
                    }
                }
            }
        }
    }
}