@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.ui.audit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.StockItem
import com.couchbase.learningpath.ui.components.*
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*

@Composable
fun AuditListView(
    viewModel: AuditListViewModel,
    navigateUp: () -> Unit,
    navigateToAuditEditor: (String, String) -> Unit,
    snackBarCoroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "${viewModel.project.name} Audits",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            }, floatingActionButton = {
                AddSubItemButton(
                    onNavClick = {
                        navigateToAuditEditor(
                            viewModel.project.projectId,
                            UUID.randomUUID().toString()
                        )
                    }
                )
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                val audits = viewModel.audits.observeAsState()
                AuditList(
                    items = audits.value,
                    onEditChange = navigateToAuditEditor,
                    onDeleteChange = viewModel.deleteAudit,
                    snackBarCoroutineScope = snackBarCoroutineScope,
                    scaffoldState = scaffoldState
                )
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun AuditList(
    items: List<Audit>?,
    onEditChange: (String, String) -> Unit,
    onDeleteChange: (String) -> Boolean,
    snackBarCoroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    if (items == null || items.isEmpty()) {
        NoItemsFound(modifier = Modifier.padding())
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items.forEach { audit ->
                item {
                    AuditCard(
                        audit = audit,
                        onEditChange = onEditChange,
                        onDeleteChange = onDeleteChange,
                        snackBarCoroutineScope = snackBarCoroutineScope,
                        scaffoldState = scaffoldState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@ExperimentalCoroutinesApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun AuditListPreview() {
    val audit = Audit(
        auditId = "",
        projectId = "",
        stockItem = StockItem("000-000-0000", name = "Test Item", description = "Test Item Description", price = 0.0F, documentType="item"),
        auditCount = 100,
        notes = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
        team = "Test Team",
        documentType = "Test Type",
        createdBy = "demo@example.com",
        modifiedBy = "demo@example.com",
        createdOn = Date(),
        modifiedOn = Date()
    )
    val auditList = listOf<Audit>() + audit + audit + audit
    val onEditChange: (String, String) -> Unit = { _: String, _: String -> }
    val onDeleteChange: (String) -> Boolean = { _: String -> false }
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coRouteScope = rememberCoroutineScope()

    AuditList(
        items = auditList,
        onEditChange = onEditChange,
        onDeleteChange = onDeleteChange,
        scaffoldState = scaffoldState,
        snackBarCoroutineScope = coRouteScope
    )
}