@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.ui.project

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.Base64

import com.couchbase.learningpath.models.Project
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.serialization.ExperimentalSerializationApi

@SuppressLint("NewApi")
@ExperimentalMaterialApi
@Composable
fun ProjectCard(project: Project,
                onProjectSelected: (String) -> Unit,
                onEditChange: (String) -> Unit,
                onDeleteChange: (String) -> Boolean,
                snackBarCoroutineScope: CoroutineScope,
                scaffoldState: ScaffoldState
)
{
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape = MaterialTheme.shapes.small,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        onClick = {
            val projectJson = Base64.getEncoder().encodeToString(project.toJson().toByteArray())
            onProjectSelected(projectJson)
        }
    ) {
        Column(
            modifier = Modifier
                .height(200.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically){
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentWidth(Alignment.Start)
                        .padding(top = 10.dp),
                    text = project.name,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically)
                        .wrapContentSize(Alignment.TopEnd)
                )
                {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false })
                    {
                        DropdownMenuItem(onClick = {
                            onEditChange(project.projectId)
                        }) {
                            Text("Edit")
                        }
                        DropdownMenuItem(onClick = {
                            val results = onDeleteChange(project.projectId)
                            expanded = false
                            if (!results) {
                                snackBarCoroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("The project was deleted from database", duration = SnackbarDuration.Short)
                                }
                            }
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
            project.warehouse?.name?.let {
                Row(modifier = Modifier
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colors.onSurface)

                    Text(modifier = Modifier.padding(start = 6.dp),
                        text = it,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = "",
                    modifier = Modifier.size(12.dp),
                    tint = if(project.isOverDue()) Color.Red else MaterialTheme.colors.onSurface)
                Text(modifier = Modifier.padding(start = 6.dp),
                    text = project.getDueDateString(),
                    style = MaterialTheme.typography.caption,
                    color = if (project.isOverDue()) Color.Red else MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                Icon(
                    Icons.Default.Group,
                    contentDescription = "",
                    modifier = Modifier.size(12.dp),
                    tint = if(project.isOverDue()) Color.Red else MaterialTheme.colors.onSurface)
                Text(modifier = Modifier.padding(start = 6.dp),
                    text = project.team,
                    style = MaterialTheme.typography.caption,
                    color = if (project.isOverDue()) Color.Red else MaterialTheme.colors.onSurface
                )
            }
            Row( modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 3,
                    modifier = Modifier.padding(top = 10.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = project.description,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun ProjectCardPreview() {
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
        modifiedOn = Date())
    val onProjectSelected: (String) -> Unit = { _ : String -> }
    val onEditChange: (String) -> Unit = { _ : String -> }
    val onDeleteChange: (String) -> Boolean  = { _: String -> false }
    val scaffoldState:ScaffoldState = rememberScaffoldState()
    val coRouteScope = rememberCoroutineScope()

    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "User Profile",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                ProjectCard(
                    project = project,
                    onProjectSelected = onProjectSelected,
                    onEditChange = onEditChange,
                    onDeleteChange = onDeleteChange,
                    snackBarCoroutineScope = coRouteScope,
                    scaffoldState = scaffoldState
                )
            }
        }
    }
}