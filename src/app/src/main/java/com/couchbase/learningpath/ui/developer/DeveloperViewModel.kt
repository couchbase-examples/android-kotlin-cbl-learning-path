@file:OptIn(ExperimentalCoroutinesApi::class)

package com.couchbase.learningpath.ui.developer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.couchbase.learningpath.data.project.ProjectRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi

@kotlinx.serialization.ExperimentalSerializationApi
class DeveloperViewModel(
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    var toastMessage = mutableStateOf("")

    val onLoadSampleData: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO){ // <1>
            projectRepository.loadSampleData()  // <2>
            toastMessage.value = "Load Sample Data Completed"
        }
    }

    val clearToastMessage: () -> Unit = {
        toastMessage.value = ""
    }
}