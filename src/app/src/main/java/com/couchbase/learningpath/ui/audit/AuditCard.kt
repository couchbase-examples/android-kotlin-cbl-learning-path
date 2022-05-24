package com.couchbase.learningpath.ui.audit

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AuditCard(audit: Audit,
              onEditChange: (String, String) -> Unit,
              onDeleteChange: (String) -> Boolean,
              snackBarCoroutineScope: CoroutineScope,
              scaffoldState: ScaffoldState)
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
            val auditJson = audit.toJson()
            onEditChange(audit.projectId, auditJson)
        }
    ){
        Column(
            modifier = Modifier
                .height(180.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically){
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentWidth(Alignment.Start),
                    text = audit.name,
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
                            val auditJson = audit.toJson()
                            onEditChange(audit.projectId, auditJson)
                        }) {
                            Text("Edit")
                        }
                        DropdownMenuItem(onClick = {
                            val results = onDeleteChange(audit.auditId)
                            expanded = false
                            if (!results) {
                                snackBarCoroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("The audit was deleted from database")
                                }
                            }
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
            audit.auditId?.let {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Id:",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface)
                    Text(
                        modifier = Modifier.padding(start = 10.dp),
                        overflow = TextOverflow.Ellipsis,
                        text = it,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
            audit.partNumber?.let {
                if (it.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SKU/Part #:",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )

                        Text(
                            modifier = Modifier.padding(start = 6.dp),
                            text = it,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
            audit.count?.let {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                    Text(text = "Count:",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface)

                    Text(modifier = Modifier.padding(start = 6.dp),
                        text = it.toString(),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
            audit.notes?.let {
                if (it.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notes:",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )
                        Text(
                            modifier = Modifier.padding(start = 10.dp),
                            overflow = TextOverflow.Ellipsis,
                            text = it,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun AuditCardPreview()
{
    val audit = Audit(
        auditId = "",
        projectId = "",
        name = "Test Audit",
        count = 100,
        notes = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        partNumber = "123456789",
        team = "Test Team",
        type = "Test Type",
        pictureMetadata =  null,
        createdBy = "demo@example.com",
        modifiedBy = "demo@example.com",
        createdOn = Date() ,
        modifiedOn = Date())

    val onEditChange: (String, String) -> Unit = { _ : String, _: String -> }
    val onDeleteChange: (String) -> Boolean  = { _: String -> false }
    val scaffoldState:ScaffoldState = rememberScaffoldState()
    val coRouteScope = rememberCoroutineScope()

    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "Audit",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                AuditCard(audit = audit,
                onEditChange = onEditChange,
                onDeleteChange = onDeleteChange,
                scaffoldState = scaffoldState,
                snackBarCoroutineScope =  coRouteScope)
            }
        }
    }
}
