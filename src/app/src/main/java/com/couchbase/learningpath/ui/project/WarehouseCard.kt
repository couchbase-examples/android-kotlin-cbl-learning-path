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

import com.couchbase.learningpath.models.Warehouse
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme

@ExperimentalMaterialApi
@Composable
fun WarehouseCard(
    warehouse: Warehouse,
    onWarehouseSelected: (Warehouse) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        onClick = {
            onWarehouseSelected(warehouse)
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
                    text = warehouse.name,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 3,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = warehouse.address1,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
            warehouse.address2?.let {
                if (it.isNotEmpty()){
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            maxLines = 3,
                            modifier = Modifier.padding(top = 2.dp),
                            overflow = TextOverflow.Ellipsis,
                            text = it,
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
                    text = "${warehouse.city}, ${warehouse.state}",
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
fun WarehouseCardPreview() {
    val warehouse = Warehouse(
        warehouseId = "",
        name = "Santa Clara Warehouse",
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
    val onWarehouseSelected: (Warehouse) -> Unit = { _: Warehouse -> }
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "Warehouse Selection",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                WarehouseCard(
                    warehouse = warehouse,
                    onWarehouseSelected = onWarehouseSelected
                )
            }
        }
    }
}