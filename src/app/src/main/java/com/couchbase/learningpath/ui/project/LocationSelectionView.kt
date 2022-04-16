package com.couchbase.learningpath.ui.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.couchbase.learningpath.models.Location
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import com.couchbase.learningpath.ui.theme.Red500

@ExperimentalMaterialApi
@Composable
fun LocationSelectionView(
    viewModel: LocationSelectionViewModel,
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
){

    LearningPathTheme {
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(
                    title = "Select Project Location",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            ){

                val onLocationSelected: (Location) -> Unit  = { location ->
                    viewModel.onLocationSelected(location)
                    navigateUp()
                }

                LocationSelector(
                    searchCity = viewModel.searchCity.value,
                    searchCountry = viewModel.searchCountry.value,
                    onSearchCityChanged = viewModel.onSearchCityChanged,
                    onSearchCountryChanged = viewModel.onSearchCountryChanged,
                    onSearch = viewModel.onSearch,
                    locationStatusMessage = viewModel.locationStatusMessage.value,
                    locations = viewModel.locationsState,
                    onLocationSelected = onLocationSelected
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun LocationSelector(
    searchCity: String,
    searchCountry: String,
    onSearchCityChanged: (String) -> Unit,
    onSearchCountryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    locationStatusMessage: String,
    locations: List<Location>,
    onLocationSelected: (Location) -> Unit
){
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)) {
        item {
            OutlinedTextField(
                value = searchCity,
                onValueChange = onSearchCityChanged,
                label = { Text("City") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            )
        }
        item {
            OutlinedTextField(
                value = searchCountry,
                onValueChange = onSearchCountryChanged,
                label = { Text("Country") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            )
        }
        item {
            Column(
                Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(modifier = Modifier
                    .padding(top = 4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
                    onClick = {
                        onSearch()
                    })
                {
                    Text("Search",
                        color = Color.White,
                        style = MaterialTheme.typography.h5)
                }
            }
        }
        if (locations.isNotEmpty()) {
            locations.forEach { location ->
                item {
                    LocationCard(
                        location = location,
                        onLocationSelected = onLocationSelected
                    )
                }
            }
        } else {
            item {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = locationStatusMessage,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun LocationSelectorPreview() {
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
    val onLocationSelected: (Location) -> Unit  = { }
    val searchCity = ""
    val searchCountry = ""
    val onSearchCityChanged: (String) -> Unit = {}
    val onSearchCountryChanged: (String) -> Unit = {}
    val onSearch: () -> Unit = { }
    val locationStatusMessage = ""
    val locationList = listOf<Location>() + location + location + location
    LearningPathTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            LocationSelector(
                searchCity = searchCity,
                searchCountry =  searchCountry,
                onSearchCityChanged = onSearchCityChanged,
                onSearchCountryChanged = onSearchCountryChanged,
                onSearch = onSearch,
                locationStatusMessage = locationStatusMessage,
                locations =  locationList,
                onLocationSelected = onLocationSelected
            )
        }
    }
}