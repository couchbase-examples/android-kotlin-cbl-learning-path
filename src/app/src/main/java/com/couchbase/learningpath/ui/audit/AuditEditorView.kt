@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.ui.audit

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.ui.components.HorizontalDottedProgressBar
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun AuditEditorView(
    viewModel: AuditEditorViewModel,
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
            InventoryAppBar(
                title = "Audit Editor",
                navigationIcon = Icons.Filled.ArrowBack,
                navigationOnClick = { navigateUp() })
        })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            ) {
                viewModel.navigateUpCallback = navigateUp
                AuditEditor(
                    audit = viewModel.auditState.value,
                    stockItemSelection = viewModel.stockItemSelectionState.value,
                    count = viewModel.count.value,
                    onCountChanged = viewModel.onCountChanged,
                    onNotesChanged = viewModel.onNotesChanged,
                    onSaveAudit = viewModel.onSaveAudit,
                    onSelectStockItem = viewModel.onStockItemSelection,
                    errorMessage = viewModel.errorMessageState.value
                )
            }
        }
    }
}

@Composable
fun AuditEditor(
    audit: Audit?,
    stockItemSelection: String,
    count: String,
    onCountChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onSaveAudit: () -> Unit,
    onSelectStockItem: () -> Unit,
    errorMessage: String
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        if (audit == null) {
            item {
                HorizontalDottedProgressBar(modifier = Modifier.padding())
            }
        } else {
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
                            text = "Stock Item:")}
                    item {
                        TextButton(
                            onClick = {
                                onSelectStockItem()
                            }) {
                            Text(stockItemSelection,
                                style = TextStyle(textDecoration = TextDecoration.Underline)
                            )
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = count,
                    onValueChange = onCountChanged,
                    label = { Text(text = "Count") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = audit.notes,
                    onValueChange = onNotesChanged,
                    label = { Text("Notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }
            item {
                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(modifier = Modifier.padding(top = 8.dp), onClick = {
                        onSaveAudit()
                    })
                    {
                        Text(
                            "Save",
                            color = Color.White,
                            style = MaterialTheme.typography.h5
                        )
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
fun AuditEditorPreview() {
    val audit = Audit()
    val stockItemSelection = "No Stock Item Selected"
    val count = "1000"
    val onCountChanged: (String) -> Unit = { }
    val onNotesChanged: (String) -> Unit = { }
    val onSaveAudit: () -> Unit = { }
    val onSelectStockItem: () -> Unit = { }
    val errorMessage = ""
    LearningPathTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            AuditEditor(
                audit = audit,
                stockItemSelection = stockItemSelection,
                count = count,
                onCountChanged = onCountChanged,
                onNotesChanged = onNotesChanged,
                onSaveAudit = onSaveAudit,
                onSelectStockItem = onSelectStockItem,
                errorMessage = errorMessage
            )
        }
    }
}

