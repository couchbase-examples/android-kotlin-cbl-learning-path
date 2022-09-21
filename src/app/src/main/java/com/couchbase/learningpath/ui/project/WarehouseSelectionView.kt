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

import com.couchbase.learningpath.models.Warehouse
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import com.couchbase.learningpath.ui.theme.Red500

@kotlinx.serialization.ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun WarehouseSelectionView(
    viewModel: WarehouseSelectionViewModel,
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
){

    LearningPathTheme {
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(
                    title = "Select Project Warehouse",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            ){

                val onWarehouseSelected: (Warehouse) -> Unit  = { warehouse ->
                    viewModel.onWarehouseSelected(warehouse)
                    navigateUp()
                }

                WarehouseSelector(
                    searchCity = viewModel.searchCity.value,
                    searchState = viewModel.searchState.value,
                    onSearchCityChanged = viewModel.onSearchCityChanged,
                    onSearchStateChanged = viewModel.onSearchStateChanged,
                    onSearch = viewModel.onSearch,
                    warehouseStatusMessage = viewModel.locationStatusMessage.value,
                    warehouses = viewModel.warehousesState,
                    onWarehouseSelected = onWarehouseSelected
                )
            }
        }
    }
}

@kotlinx.serialization.ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun WarehouseSelector(
    searchCity: String,
    searchState: String,
    onSearchCityChanged: (String) -> Unit,
    onSearchStateChanged: (String) -> Unit,
    onSearch: () -> Unit,
    warehouseStatusMessage: String,
    warehouses: List<Warehouse>,
    onWarehouseSelected: (Warehouse) -> Unit
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
                value = searchState,
                onValueChange = onSearchStateChanged,
                label = { Text("State") },
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
        if (warehouses.isNotEmpty()) {
            warehouses.forEach { warehouse ->
                item {
                    WarehouseCard(
                        warehouse = warehouse,
                        onWarehouseSelected = onWarehouseSelected
                    )
                }
            }
        } else {
            item {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = warehouseStatusMessage,
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@kotlinx.serialization.ExperimentalSerializationApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
private fun WarehouseSelectorPreview() {
    val warehouse = Warehouse(
        warehouseId = "",
        name = "Santa Clara",
        address1 = "123 No Where Street",
        address2 = "Box 123",
        city = "Santa Clara",
        state = "CA",
        postalCode = "123456",
        salesTax = 0.00,
        yearToDateBalance = 0.00,
        latitude = 123.12,
        longitude = 123.12,
        documentType = "warehouse",
        shippingTo = listOf("")
    )
    val onWarehouseSelected: (Warehouse) -> Unit  = { }
    val searchCity = ""
    val searchCountry = ""
    val onSearchCityChanged: (String) -> Unit = {}
    val onSearchCountryChanged: (String) -> Unit = {}
    val onSearch: () -> Unit = { }
    val warehouseStatusMessage = ""
    val warehouseList = listOf<Warehouse>() + warehouse + warehouse + warehouse
    LearningPathTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            WarehouseSelector(
                searchCity = searchCity,
                searchState =  searchCountry,
                onSearchCityChanged = onSearchCityChanged,
                onSearchStateChanged = onSearchCountryChanged,
                onSearch = onSearch,
                warehouseStatusMessage = warehouseStatusMessage,
                warehouses =  warehouseList,
                onWarehouseSelected = onWarehouseSelected
            )
        }
    }
}