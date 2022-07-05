@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.ui.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.couchbase.learningpath.models.Project
import com.couchbase.learningpath.ui.components.DatePicker
import com.couchbase.learningpath.ui.components.HorizontalDottedProgressBar
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import com.couchbase.learningpath.ui.theme.Red500
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun ProjectEditorView(
    viewModel: ProjectEditorViewModel,
    navigateUp: () -> Unit,
    navigateToListSelection: (String) -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
)
{
    LearningPathTheme {
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(
                    title = "Project Editor",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            ){
                viewModel.navigateUpCallback = navigateUp
                viewModel.navigateToListSelection = navigateToListSelection

                ProjectEditor(
                    project = viewModel.projectState.value,
                    locationSelection = viewModel.warehouseSelectionState.value,
                    dueDate = viewModel.dueDateState.value,
                    onNameChange = viewModel.onNameChanged,
                    onDescriptionChange = viewModel.onDescriptionChanged,
                    onDateChanged = viewModel.onDateChanged,
                    onSaveProject = viewModel.onSaveProject,
                    errorMessage = viewModel.errorMessageState.value
                )
            }
        }
    }
}

@Composable
fun ProjectEditor(
    project: Project?,
    locationSelection: String,
    dueDate: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChanged: (Long?) -> Unit,
    onSaveProject: (navigateUp: Boolean) -> Unit,
    errorMessage: String
){
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(16.dp)) {
        if (project == null) {
            item {
                HorizontalDottedProgressBar(modifier = Modifier.padding())
            }
        } else {
            item {
                OutlinedTextField(
                    value = project.name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = project.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }
            item {
                DatePicker(selectedDate = dueDate, onDateChanged = onDateChanged)
            }
            item {
                LazyRow(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start) {
                    item {
                        Text(
                            modifier = Modifier.padding(end = 16.dp),
                            text = "Location:")}
                    item {
                        TextButton(
                            onClick = {
                                onSaveProject(false)
                            }) {
                            Text(locationSelection,
                                style = TextStyle(textDecoration = TextDecoration.Underline)
                            )
                        }
                    }
                }
            }
            item {
                Column(
                    Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(modifier = Modifier
                        .padding(top = 24.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
                        onClick = {
                            onSaveProject(true)
                        })
                    {
                        Text("Save",
                            color = Color.White,
                            style = MaterialTheme.typography.h5)
                    }
                }
            }
            if (errorMessage.isNotEmpty()){
                item {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = errorMessage,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProjectEditorPreview() {
    val project = Project()
    val onNameChange: (String) -> Unit = {}
    val dueDate = "Due Date"
    val locationSelect = "No Location Selected"
    val onDescriptionChange: (String) -> Unit = { }
    val onSaveProject: (navigateUp: Boolean) -> Unit  =  { }
    val onDateChanged: (Long?) -> Unit = {}
    val errorMessage = ""

    LearningPathTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            ProjectEditor(project = project,
                locationSelection = locationSelect,
                onNameChange = onNameChange,
                onDescriptionChange = onDescriptionChange,
                onSaveProject = onSaveProject,
                dueDate = dueDate,
                onDateChanged = onDateChanged,
                errorMessage = errorMessage)
        }
    }
}