package com.couchbase.learningpath.ui.developer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.couchbase.learningpath.services.ReplicatorServiceMock
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.components.LabelSwitchRow
import com.couchbase.learningpath.ui.components.RowButton
import com.couchbase.learningpath.ui.components.RowListSelection
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.coroutines.InternalCoroutinesApi

@Composable
fun ReplicatorConfigView(
    viewModel: ReplicatorConfigViewModel,
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState())
{
    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "Replicator Config",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() }
                )
            })

        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            ) {
                ReplicatorConfigOptions(
                    serverUrl = viewModel.serverUrlState.value,
                    onServerUrlChanged = viewModel.onServerUrlChanged,
                    heartBeat = viewModel.heartBeatState.value.toString(),
                    onHeartBeatChanged = viewModel.onHeartBeatChanged,
                    isContinuous = viewModel.continuousState.value,
                    onContinuousChanged = viewModel.onContinuousChanged,
                    useSelfSignedCert = viewModel.selfSignedCertState.value,
                    onUseSelfSignedCert = viewModel.onSelfSignedCertChanged,
                    replicatorTypes = viewModel.replicatorTypes,
                    replicatorTypeSelected = viewModel.replicatorTypeSelected.value,
                    onReplicatorTypeChanged = viewModel.onReplicatorTypeChanged,
                    username = viewModel.username,
                    password = viewModel.password,
                    onSave = viewModel::save,
                    navigateUp = navigateUp)
            }
        }
    }
}

@Composable
fun ReplicatorConfigOptions(
    serverUrl: String,
    onServerUrlChanged: (String) -> Unit,
    heartBeat: String,
    onHeartBeatChanged: (Long) -> Unit,
    isContinuous: Boolean,
    onContinuousChanged: (Boolean) -> Unit,
    useSelfSignedCert: Boolean,
    onUseSelfSignedCert: (Boolean) -> Unit,
    replicatorTypes: List<String>,
    replicatorTypeSelected: String,
    onReplicatorTypeChanged: (String) -> Unit,
    username: String,
    password: String,
    onSave: () -> Unit,
    navigateUp: () -> Unit)
{
    val rowPadding = PaddingValues(top = 20.dp, start = 16.dp, end = 16.dp)

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        item  {
            Row(modifier = Modifier
                .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 4.dp)
                .fillMaxWidth()
            ){
                Text("Configuration", style = MaterialTheme.typography.subtitle1)
            }
        }
        item{
            Row(modifier = Modifier
                .padding(rowPadding)
                .fillMaxWidth()
            ) {
                TextField(modifier = Modifier.fillMaxWidth(),
                    value = serverUrl,
                    onValueChange = {
                        onServerUrlChanged(it)
                    },
                    label = { Text("Sync Gateway Server URL") })
            }
        }
        item{
            Row(modifier = Modifier
                .padding(rowPadding)
                .fillMaxWidth())
            {
                TextField(modifier = Modifier.fillMaxWidth(),
                    value = heartBeat,
                    onValueChange =   {
                        it.toLongOrNull()?.let { value ->
                            onHeartBeatChanged(value)
                        }
                    },
                    label = { Text("Heartbeat (in Seconds)")}
                )
            }
        }
        item {
            Row(modifier = Modifier
                .padding(rowPadding)
                .fillMaxWidth()) {
                Text("Replicator Type", style = MaterialTheme.typography.caption)
            }
        }
        item{
            RowListSelection(
                selection = replicatorTypeSelected,
                items = replicatorTypes,
                onSelectionChanged = { onReplicatorTypeChanged(it) },
                imageVector = Icons.Default.Sync)
        }
        item {
            LabelSwitchRow(
                text = "Continuous",
                isSwitched = isContinuous,
                onCheckChanged = { onContinuousChanged(it) }
            )
        }
        item {
            LabelSwitchRow(
                text = "Accept Only Self-Signed Certs",
                isSwitched = useSelfSignedCert,
                onCheckChanged = { onUseSelfSignedCert(it) }
            )
        }
        item {
            DividerRow()
        }
        item {
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                Text("Authentication", style = MaterialTheme.typography.h6)
            }
        }
        item {
            Row(modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
                Text("Username: $username")
            }
        }
        item {
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                Text("Password: $password")
            }
        }
        item {
            DividerRow()
        }
        item {
            RowButton ( onClick = {
                //save and head back
                onSave()
                //navigate back
                navigateUp()
            }, displayText = "Save")
        }
    }
}

@Composable
fun DividerRow() {
    Row(modifier = Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 12.dp)) {
        Divider(color = Color.DarkGray, thickness = 1.dp)
    }
}

@OptIn(InternalCoroutinesApi::class)
@Preview(showBackground = true)
@Composable
fun ReplicationConfigScreenPreview(){
    val viewModel = ReplicatorConfigViewModel(ReplicatorServiceMock())
    LearningPathTheme {
        ReplicatorConfigView(viewModel = viewModel, navigateUp = { })
    }
}