package com.couchbase.learningpath.data.stockItem

import android.content.Context
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.models.StockItem

class StockItemRepositoryDb(context: Context)
    : StockItemRepository {
    private val databaseResources: DatabaseManager = DatabaseManager.getInstance(context)
    private val documentType = "item"
    private val nameAttributeName = "name"
    private val descriptionAttributeName = "description"
    private val typeAttributeName = "documentType"

    override val databaseName: () -> String? =  { DatabaseManager.getInstance(context).warehouseDatabase?.name }
    override val databaseLocation: () -> String? = { DatabaseManager.getInstance(context).warehouseDatabase?.path }

    override suspend fun get(): List<StockItem> {
        TODO("Not yet implemented")
    }

    override suspend fun count(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getByNameDescription(
        searchName: String,
        searchDescription: String?
    ): List<StockItem> {
        TODO("Not yet implemented")
    }
}