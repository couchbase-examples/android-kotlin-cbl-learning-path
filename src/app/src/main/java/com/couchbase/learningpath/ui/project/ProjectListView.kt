@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.ui.project

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

import com.couchbase.learningpath.models.Project
import com.couchbase.learningpath.R
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.components.AddButton
import com.couchbase.learningpath.ui.components.HorizontalDottedProgressBar
import com.couchbase.learningpath.ui.components.NoItemsFound
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.serialization.ExperimentalSerializationApi


@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun ProjectListView(
    openDrawer: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    navigateToProjectEditor: (String) -> Unit,
    navigateToAuditListByProject: (String) -> Unit,
    viewModel: ProjectListViewModel) {
    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title =  stringResource(id = R.string.projects),
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { openDrawer() })
            }, floatingActionButton = { AddButton(navigateToProjectEditor) })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                // collecting the flow and turning it into state
                // https://developer.android.com/jetpack/compose/libraries#streams
                val projectList by viewModel.repositoryFlow.collectAsState(initial = listOf())

                ProjectList(
                    items = projectList,
                    isLoading = viewModel.isLoading.value,
                    onProjectSelected = navigateToAuditListByProject,
                    onEditChange = navigateToProjectEditor,
                    onDeleteChange = viewModel.deleteProject,
                    scaffoldState =  scaffoldState,
                    scope = scope
                )
            }
        }
    }
}


@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun ProjectList(
    items: List<Project>,
    isLoading: Boolean,
    onProjectSelected: (String) -> Unit,
    onEditChange: (String) -> Unit,
    onDeleteChange: (String) -> Boolean,
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
) {
    LazyColumn(
        modifier = Modifier.padding(16.dp)
    ) {

        // changes between state will load super fast on emulator - in a real app
        // probably better to animate between them with a library like shimmer
        if (isLoading && items.isEmpty()) {
            item {
                HorizontalDottedProgressBar(modifier = Modifier.padding())
            }
        } else if (items.isEmpty()) {
            item {
                NoItemsFound(modifier = Modifier.padding())
            }
        } else {
            items.forEach { project ->
                item {
                    ProjectCard(
                        project = project,
                        onProjectSelected = onProjectSelected,
                        onEditChange = onEditChange,
                        onDeleteChange = onDeleteChange,
                        snackBarCoroutineScope = scope,
                        scaffoldState = scaffoldState
                    )

                    Spacer(modifier = Modifier.padding(top = 30.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@Composable
fun ProjectListPreview() {
    val project = Project(
        projectId = "",
        name = "Test Project",
        description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        isComplete = false,
        documentType = "project",
        dueDate = Date(),
        warehouse = null,
        team = "Test Team",
        createdBy = "demo@example.com",
        modifiedBy = "demo@example.com",
        createdOn = Date(),
        modifiedOn = Date()
    )
    val projectList = listOf<Project>() + project + project + project
    val onProjectSelected: (String) -> Unit = { _ : String -> }
    val onEditChange: (String) -> Unit = { _ : String -> }
    val onDeleteChange: (String) -> Boolean  = { _: String -> false }
    val scaffoldState:ScaffoldState = rememberScaffoldState()
    val coRouteScope = rememberCoroutineScope()

    ProjectList(
        items = projectList,
        isLoading = false,
        onProjectSelected = onProjectSelected,
        onEditChange = onEditChange,
        onDeleteChange = onDeleteChange,
        scaffoldState = scaffoldState,
        scope = coRouteScope)

}