package com.couchbase.learningpath.ui.developer

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import com.couchbase.learningpath.ui.theme.Red500

//this screen shows stats about information in the database and location of the database
@kotlinx.serialization.ExperimentalSerializationApi
@Composable
fun DeveloperView(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: DeveloperViewModel,
    openDrawer: () -> Unit,
    navigateToDatabaseInfoView: () -> Unit
) {
    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "Developer",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { openDrawer() })
            })

        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                DeveloperWidget(
                    toastMessage = viewModel.toastMessage.value,
                    onLoadSampleData = viewModel.onLoadSampleData,
                    clearToastMessage = viewModel.clearToastMessage,
                    navigateToDatabaseInfoView = navigateToDatabaseInfoView
                )
            }
        }
    }
}

@kotlinx.serialization.ExperimentalSerializationApi
@Composable
fun DeveloperWidget(
    toastMessage: String?,
    onLoadSampleData: () -> Unit,
    clearToastMessage: () -> Unit,
    navigateToDatabaseInfoView:  () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 10.dp)
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .semantics { contentDescription = "database_information" },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
                    onClick = {
                        navigateToDatabaseInfoView()
                    })
                {
                    Text(
                        "Database Information",
                        style = MaterialTheme.typography.h5,
                        color = Color.White
                    )
                }
            }
        }
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .padding(top = 32.dp)
                        .semantics { contentDescription = "load_sample_data" },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
                    onClick = {
                        onLoadSampleData()
                    })
                {
                    Text(
                        "Load Sample Data",
                        style = MaterialTheme.typography.h5,
                        color = Color.White
                    )
                }
            }
        }
    }
    toastMessage?.let {
        if (it.isNotEmpty()) {
            Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
            clearToastMessage()
        }
    }
}

@kotlinx.serialization.ExperimentalSerializationApi
@Preview(showBackground = true)
@Composable
fun DeveloperWidgetPreview() {
    DeveloperWidget(
        toastMessage = "",
        onLoadSampleData = { },
        clearToastMessage = { },
        navigateToDatabaseInfoView =  { }
    )
}