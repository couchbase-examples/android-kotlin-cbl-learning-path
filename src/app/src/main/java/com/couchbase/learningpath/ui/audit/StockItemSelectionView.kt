package com.couchbase.learningpath.ui.audit

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

import com.couchbase.learningpath.models.StockItem
import com.couchbase.learningpath.models.Warehouse
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.project.WarehouseCard
import com.couchbase.learningpath.ui.project.WarehouseSelector
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import com.couchbase.learningpath.ui.theme.Red500

@kotlinx.serialization.ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun StockItemSelectionView(
    viewModel: StockItemSelectionViewModel,
    navigateUp: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
){

    LearningPathTheme {
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(
                    title = "Select Stock Item",
                    navigationIcon = Icons.Filled.ArrowBack,
                    navigationOnClick = { navigateUp() })
            }
        )
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            ){

                val onStockItemSelected: (StockItem) -> Unit  = { stockItem ->
                    viewModel.onStockItemSelected(stockItem)
                }

                StockItemSelector(
                    searchName = viewModel.searchName.value,
                    searchDescription = viewModel.searchDescription.value,
                    onSearchNameChanged = viewModel.onSearchNameChanged,
                    onSearchDescriptionChanged = viewModel.onSearchDescriptionChanged,
                    onSearch = viewModel.onSearch,
                    stockItemStatusMessage = viewModel.statusMessage.value,
                    stockItems = viewModel.stockItemsState,
                    onStockItemSelected = onStockItemSelected
                )
            }
        }
    }
}
@kotlinx.serialization.ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun StockItemSelector(
    searchName: String,
    searchDescription: String,
    onSearchNameChanged: (String) -> Unit,
    onSearchDescriptionChanged: (String) -> Unit,
    onSearch: () -> Unit,
    stockItemStatusMessage: String,
    stockItems: List<StockItem>,
    onStockItemSelected: (StockItem) -> Unit) {
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)) {
        item {
            OutlinedTextField(
                value = searchName,
                onValueChange = onSearchNameChanged,
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
            )
        }
        item {
            OutlinedTextField(
                value = searchDescription,
                onValueChange = onSearchDescriptionChanged,
                label = { Text("Description") },
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
        if (stockItems.isNotEmpty()) {
            stockItems.forEach { stockItem ->
                item {
                    StockItemCard(
                        stockItem = stockItem,
                        onStockItemSelected = onStockItemSelected
                    )
                }
            }
        } else {
            item {
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = stockItemStatusMessage,
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
private fun StockItemSelectorPreview() {
    val stockItem = StockItem(
        itemId = "",
        name = "Test Item",
        description = "Test Description",
        price = 0.00F,
        documentType = "item"
    )
    val onStockItemSelected: (StockItem) -> Unit  = { }
    val searchName = ""
    val searchDescription = ""
    val onSearchNameChanged: (String) -> Unit = {}
    val onSearchDescriptionChanged: (String) -> Unit = {}
    val onSearch: () -> Unit = { }
    val stockItemStatusMessage = ""
    val stockItemList = listOf<StockItem>() + stockItem + stockItem + stockItem
    LearningPathTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            StockItemSelector(
                searchName = searchName,
                searchDescription = searchDescription,
                onSearchNameChanged = onSearchNameChanged,
                onSearchDescriptionChanged = onSearchDescriptionChanged,
                onSearch = onSearch,
                stockItemStatusMessage = stockItemStatusMessage,
                stockItems = stockItemList,
                onStockItemSelected = onStockItemSelected
            )
        }
    }
}
