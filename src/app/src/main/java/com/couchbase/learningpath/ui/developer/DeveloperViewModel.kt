package com.couchbase.learningpath.ui.developer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.couchbase.learningpath.data.project.ProjectRepository

class DeveloperViewModel(
    private val projectRepository: ProjectRepository,
) : ViewModel() {

    var toastMessage = mutableStateOf("")

    val onLoadSampleData: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO){
            projectRepository.loadSampleData()
            toastMessage.value = "Load Sample Data Completed"
        }
    }

    val clearToastMessage: () -> Unit = {
        toastMessage.value = ""
    }
}