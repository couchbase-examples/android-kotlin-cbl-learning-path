@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.data.stockItem

import android.util.Log
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
    private val databaseManager: DatabaseManager
) : StockItemRepository {
    private val documentType = "item"

    override val databaseName: () -> String? =
        { databaseManager.warehouseDatabase?.name }
    override val databaseLocation: () -> String? =
        { databaseManager.warehouseDatabase?.path }

    override suspend fun get(): List<StockItem> {
        return withContext(Dispatchers.IO) {
            val stockItems = mutableListOf<StockItem>()
            try {
                val db = databaseManager.warehouseDatabase
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
                val db = databaseManager.warehouseDatabase
                db?.let { database ->
                    val query =
                        database.createQuery("SELECT COUNT(*) AS count FROM _ AS item WHERE documentType=\"$documentType\"") // 1
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
        return withContext(Dispatchers.IO) {
            val stockItems = mutableListOf<StockItem>()
            try {
                val db = databaseManager.warehouseDatabase
                db?.let { database ->
                    var queryString =
                        "SELECT * FROM _ as item WHERE documentType=\"item\" AND lower(name) LIKE ('%' || \$parameterName || '%')"  // 1
                    var parameters = Parameters() // 2
                    parameters.setString("parameterName", searchName.lowercase()) // 3
                    searchDescription?.let { description ->
                        if (description.isNotEmpty()) {  // 4
                            queryString =
                                queryString.plus(" AND lower(description) LIKE ('%' || \$parameterDescription || '%')")  // 5
                            parameters.setString(
                                "parameterDescription",
                                searchDescription.lowercase()
                            ) // 6
                        }
                    }
                    var query = database.createQuery(queryString) // 7
                    query.parameters = parameters // 8
                    var results = query.execute().allResults() // 9
                    results.forEach { result ->  // 10
                        val stockItem =
                            Json.decodeFromString<StockItemDao>(result.toJSON()).item // 11
                        stockItems.add(stockItem) // 12
                    }
                }

            } catch (e: java.lang.Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext stockItems
        }
    }
}