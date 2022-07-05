@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.data.stockItem

import com.couchbase.learningpath.models.StockItem
import kotlinx.serialization.ExperimentalSerializationApi

interface StockItemRepository {

    val databaseName: () -> String?
    val databaseLocation:() -> String?

    suspend fun get() : List<StockItem>
    suspend fun count(): Int

    suspend fun getByNameDescription(
        searchName: String,
        searchDescription: String?): List<StockItem>

}