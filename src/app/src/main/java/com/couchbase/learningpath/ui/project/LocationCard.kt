package com.couchbase.learningpath.ui.project

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

import com.couchbase.learningpath.models.Location
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme

@ExperimentalMaterialApi
@Composable
fun LocationCard(
    location: Location,
    onLocationSelected: (Location) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        onClick = {
            onLocationSelected(location)
        }
    ) {
        Column(
            modifier = Modifier
                .height(120.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .wrapContentWidth(Alignment.Start)
                        .padding(top = 4.dp),
                    text = location.name,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 3,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = location.address1,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
            location.address2?.let {
                if (it.isNotEmpty()){
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            maxLines = 3,
                            modifier = Modifier.padding(top = 2.dp),
                            overflow = TextOverflow.Ellipsis,
                            text = location.address2,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 3,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = "${location.city}, ${location.state}  ${location.country}",
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
fun LocationCardPreview() {
    val location = Location(
        locationId = "",
        name = "Santa Clara",
        address1 = "123 No Where Street",
        address2 = "Box 123",
        city = "Santa Clara",
        state = "CA",
        country = "US",
        postalCode = "123456",
        latitude = 123.12,
        longitude = 123.12,
        type = "location"
    )
    val onLocationSelected: (Location) -> Unit = { _: Location -> }
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    LearningPathTheme() {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "Location Selection",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                LocationCard(
                    location = location,
                    onLocationSelected = onLocationSelected
                )
            }
        }
    }
}