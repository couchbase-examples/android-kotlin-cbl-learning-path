package com.couchbase.learningpath.data.warehouse

import com.couchbase.learningpath.models.Warehouse

interface WarehouseRepository {
    val warehouseDatabaseName: () -> String?
    val warehouseDatabaseLocation:() -> String?

    suspend fun getByCityState(
        searchCity: String,
        searchState: String?): List<Warehouse>

    suspend fun get(): List<Warehouse>

    suspend fun warehouseCount(): Int
}