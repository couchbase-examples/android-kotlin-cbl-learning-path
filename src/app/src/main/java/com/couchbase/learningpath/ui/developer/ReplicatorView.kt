package com.couchbase.learningpath.ui.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.couchbase.learningpath.services.ReplicatorServiceMock
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.coroutines.InternalCoroutinesApi

@Composable
fun ReplicatorView(
    viewModel: ReplicatorViewModel,
    openDrawer: () -> Unit,
    replicatorConfigNav: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState())
{
    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "Replicator Status",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { openDrawer() },
                    menuAction = {
                        IconButton(onClick = { replicatorConfigNav() }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(end = 10.dp)
                            )
                        }
                    }
                )
            })

        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                ReplicatorWidget(
                    replicationStatus = viewModel.replicationStatus.value,
                    replicationProgress = viewModel.replicationProgress.value,
                    logMessage = viewModel.logMessages,
                    isButtonShown = viewModel.isButtonActive.value,
                    onStartClick = viewModel::onStartClick,
                    onStopClick = viewModel::onStopClick,
                    onClearLogs = viewModel::clearLogs
                )
            }
        }
    }
}

@Composable
fun ReplicatorWidget(
    replicationStatus: String,
    replicationProgress: String,
    logMessage: List<String>,
    isButtonShown: Boolean,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onClearLogs: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp)
    ){
        item {
            Text(text = "Replicator Status: $replicationStatus")
        }
        item {
            Text(
                text = "Replicator Progress: $replicationProgress",
                modifier = Modifier.padding(top = 10.dp)
            )
        }
        item {
            ReplicatorDivider()
        }
        if (isButtonShown) {
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(modifier = Modifier
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        onClick = {
                            onStartClick()
                        })
                    {
                        Text("Start",
                            color = Color.White,
                            style = MaterialTheme.typography.h5)
                    }
                    Button(modifier = Modifier
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        onClick = {
                            onStopClick()
                        })
                    {
                        Text("Stop",
                            color = Color.White,
                            style = MaterialTheme.typography.h5)
                    }
                    Button(modifier = Modifier
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        onClick = { onClearLogs() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp, 30.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            item {
                ReplicatorDivider()
            }
            logMessage.forEach {
                item {
                    Text(it)
                }
            }
        }
    }
}

@Composable
fun ReplicatorDivider() {
    Divider(color = Color.DarkGray,
        thickness = 1.dp,
        modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
}

@OptIn(InternalCoroutinesApi::class)
@Preview(showBackground = true)
@Composable
fun ReplicatorViewPreview(){
    val viewModel = ReplicatorViewModel(ReplicatorServiceMock())
    LearningPathTheme {
        ReplicatorWidget(
            replicationStatus = viewModel.replicationStatus.value,
            replicationProgress = viewModel.replicationProgress.value,
            logMessage =  viewModel.logMessages,
            isButtonShown = true,
            onStartClick = viewModel::onStartClick,
            onStopClick = viewModel::onStopClick,
            onClearLogs = viewModel::clearLogs
        )
    }
}