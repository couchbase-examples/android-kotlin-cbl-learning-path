@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.data.stockItem

import android.content.Context
import android.util.Log
import androidx.compose.ui.text.toLowerCase
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.models.StockItem
import com.couchbase.learningpath.models.StockItemDao
import com.couchbase.lite.Parameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class StockItemRepositoryDb(
    var context: Context) : StockItemRepository {
    private val databaseResources: DatabaseManager = DatabaseManager.getInstance(context)
    private val documentType = "item"

    override val databaseName: () -> String? =  { DatabaseManager.getInstance(context).warehouseDatabase?.name }
    override val databaseLocation: () -> String? = { DatabaseManager.getInstance(context).warehouseDatabase?.path }

    override suspend fun get(): List<StockItem> {
        return withContext(Dispatchers.IO) {
            val stockItems = mutableListOf<StockItem>()
            try {
                val db = databaseResources.warehouseDatabase
                db?.let { database ->
                    val query =
                        database.createQuery("SELECT * FROM _ AS item WHERE documentType=\"$documentType\"")
                    var results = query.execute().allResults()
                    results.forEach { result ->
                        val stockItem = Json.decodeFromString<StockItemDao>(result.toJSON()).item
                        stockItems.add(stockItem)
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext stockItems
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            try {
                val db = DatabaseManager.getInstance(context).warehouseDatabase
                db?.let { database ->
                    val query =  database.createQuery("SELECT COUNT(*) AS count FROM _ AS item WHERE documentType=\"$documentType\"") // 1
                    val results = query.execute().allResults() // 2
                    count = results[0].getInt("count") // 3
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext count
        }
    }

    override suspend fun getByNameDescription(
        searchName: String,
        searchDescription: String?
    ): List<StockItem> {
        val stockItems = mutableListOf<StockItem>()
        try {
            val db = databaseResources.warehouseDatabase
            db?.let { database ->
                var queryString = "SELECT * FROM _ as item WHERE documentType=\"item\" AND lower(name) LIKE '%${searchName.lowercase()}%'"  // 1
                searchDescription?.let { description ->
                    if(description.isNotEmpty()){ //2
                        queryString = queryString.plus( " AND lower(description) LIKE '%${description.lowercase()}%'")  // 3
                    }
                    var query = database.createQuery(queryString) // 4
                    var results = query.execute().allResults() // 2
                    results.forEach { result ->  // 6
                        val stockItem = Json.decodeFromString<StockItemDao>(result.toJSON()).item // 7
                        stockItems.add(stockItem) // 8
                    }
                }
            }

        } catch (e: java.lang.Exception){
            Log.e(e.message, e.stackTraceToString())
        }
        return stockItems
    }
}