@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.ui.audit

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.ui.components.HorizontalDottedProgressBar
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.serialization.ExperimentalSerializationApi

@Composable
fun AuditEditorView(
    viewModel: AuditEditorViewModel,
    projectId: String,
    auditJson: String,
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    if (viewModel.projectId.value == "") {
        viewModel.getAudit(projectId = projectId, auditJson = auditJson)
    }
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
                    audit = viewModel.audit.value,
                    count = viewModel.count.value,
                    onNameChanged = viewModel.onNameChanged,
                    onCountChanged = viewModel.onCountChanged,
                    onNotesChanged = viewModel.onNotesChanged,
                    onPartNumberChanged = viewModel.onPartNumberChanged,
                    onSaveAudit = viewModel.onSaveAudit,
                )
            }
        }
    }
}

@Composable
fun AuditEditor(
    audit: Audit?,
    count: String,
    onNameChanged: (String) -> Unit,
    onCountChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onPartNumberChanged: (String) -> Unit,
    onSaveAudit: () -> Unit
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
                OutlinedTextField(
                    value = audit.name,
                    onValueChange = onNameChanged,
                    label = { Text(text = "Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
            }
            item {
                OutlinedTextField(
                    value = audit.partNumber,
                    onValueChange = onPartNumberChanged,
                    label = { Text(text = "Part Number/SKU") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                )
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
        }
    }
}

