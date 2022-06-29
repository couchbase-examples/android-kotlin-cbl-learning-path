@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalSerializationApi::class)

package com.couchbase.learningpath.ui.project

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.models.Project
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi

class ProjectEditorViewModel(
    private val repository: ProjectRepository,
) : ViewModel() {

    private val defaultWarehouseText: String = "No Warehouse Selected"
    var projectState = mutableStateOf<Project?>(null)

    val dueDateState = mutableStateOf("Select Due Date")
    val warehouseSelectionState = mutableStateOf(defaultWarehouseText)
    val errorMessageState = mutableStateOf("")
    var navigateUpCallback: () -> Unit  = { }
    var navigateToListSelection: (String) -> Unit = { }

    val projectId: (String) -> Unit = {
        viewModelScope.launch {
            val project = repository.get(it)
            withContext(Dispatchers.Main) {
                projectState.value = project
                project.dueDate?.let { dueDate ->
                    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                    dueDateState.value = formatter.format(dueDate)
                }
                if (project.warehouse != null) {
                    project.warehouse?.let {
                        warehouseSelectionState.value = it.name
                    }
                } else {
                    warehouseSelectionState.value = defaultWarehouseText
                }
            }
        }
    }

    private fun dateFormatter(milliseconds: Long): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return formatter.format(calendar.time)
    }

    val onDateChanged: (Long?) -> Unit = { date ->
        date?.let { theDate ->
            dueDateState.value = dateFormatter(theDate)
            projectState.value?.dueDate = Date(theDate)
        }
    }

    val onNameChanged: (String) -> Unit = { newValue ->
        val p = projectState.value?.copy()
        p?.name = newValue
        projectState.value = p
    }

    val onDescriptionChanged: (String) -> Unit = { newValue ->
        val p = projectState.value?.copy()
        p?.description = newValue
        projectState.value = p
    }

    val onSaveProject: (navigateUp: Boolean) -> Unit = { navigateUp ->
        viewModelScope.launch(Dispatchers.IO) {
            projectState.value?.let { project ->
                if (project.name.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        errorMessageState.value = "Error:  Must enter a name before continuing"
                    }
                } else {
                    errorMessageState.value = ""
                    //save value than figure out which place to navigate to, either main project list
                    //or to the list selection screen
                    repository.save(project)

                    withContext(Dispatchers.Main) {
                        if (navigateUp) {
                            navigateUpCallback()
                        } else {
                            navigateToListSelection(project.projectId)
                        }
                    }
                }
            }
        }
    }
}