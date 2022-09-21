package com.couchbase.learningpath.ui.audit
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

import com.couchbase.learningpath.models.StockItem
import com.couchbase.learningpath.ui.components.InventoryAppBar
import com.couchbase.learningpath.ui.theme.LearningPathTheme

@kotlinx.serialization.ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun StockItemCard(
    stockItem: StockItem,
    onStockItemSelected: (StockItem) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.small,
        backgroundColor = MaterialTheme.colors.surface,
        modifier = Modifier
            .padding(top = 6.dp, bottom = 6.dp)
            .fillMaxWidth(),
        elevation = 8.dp,
        onClick = {
            onStockItemSelected(stockItem)
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
                    text = stockItem.name,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onSurface
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    maxLines = 4,
                    modifier = Modifier.padding(top = 2.dp),
                    overflow = TextOverflow.Ellipsis,
                    text = stockItem.description,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@kotlinx.serialization.ExperimentalSerializationApi
@Preview(showBackground = true)
@Composable
fun StockItemCardPreview() {
    val stockItem = StockItem(
        itemId = "",
        name = "Test Item",
        description = "Test Description",
        price = 0.00F,
        documentType = "item"
    )
    val onStockItemSelected: (StockItem) -> Unit = { _: StockItem -> }
    val scaffoldState: ScaffoldState = rememberScaffoldState()

    LearningPathTheme {
        // A surface container using the 'background' color from the theme
        Scaffold(scaffoldState = scaffoldState,
            topBar = {
                InventoryAppBar(title = "Stock Item Selection",
                    navigationIcon = Icons.Filled.Menu,
                    navigationOnClick = { })
            })
        {
            Surface(
                color = MaterialTheme.colors.background,
                modifier = Modifier.fillMaxSize()
            )
            {
                StockItemCard(
                    stockItem = stockItem,
                    onStockItemSelected = onStockItemSelected
                )
            }
        }
    }
}