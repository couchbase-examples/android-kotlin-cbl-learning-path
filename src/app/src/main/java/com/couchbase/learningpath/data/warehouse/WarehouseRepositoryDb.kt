package com.couchbase.learningpath.data.warehouse

import android.util.Log
import com.couchbase.lite.*
import com.couchbase.lite.Function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.models.Warehouse
import com.couchbase.learningpath.models.WarehouseDao

class WarehouseRepositoryDb(
    private val databaseManager: DatabaseManager
) : WarehouseRepository {
    private val documentType = "warehouse"

    private val cityAttributeName = "city"
    private val stateAttributeName = "state"
    private val typeAttributeName = "documentType"
    private val itemAliasName = "item"

    override suspend fun getByCityState(searchCity: String, searchState: String?): List<Warehouse> {
        return withContext(Dispatchers.IO){
            val warehouses = mutableListOf<Warehouse>()
            try {
                val db = databaseManager.warehouseDatabase
                db?.let { database ->
                    //search by city
                    var whereQueryExpression = Function
                        .lower(Expression.property(cityAttributeName)).like(Expression.string("%" + searchCity.lowercase() + "%")) // <1>

                    //search by optional state
                    searchState?.let { state ->
                        if (state.isNotEmpty()) {
                            val stateQueryExpression = Function
                                .lower(Expression.property(stateAttributeName))
                                .like(Expression.string("%" + state.lowercase() + "%"))  // <2>

                            whereQueryExpression =
                                whereQueryExpression.and(stateQueryExpression)  // <2>
                        }
                    }

                    //add type filter
                    val typeQueryExpression = Function
                        .lower(Expression.property(typeAttributeName))
                        .equalTo(Expression.string(documentType))  // <3>
                    whereQueryExpression = whereQueryExpression.and(typeQueryExpression) // <3>

                    //create query to execute using QueryBuilder API
                    val query = QueryBuilder
                        .select(SelectResult.all())
                        .from(DataSource.database(database).`as`(itemAliasName))
                        .where(whereQueryExpression) // <4>

                    //loop through results and add to list
                    query.execute().allResults().forEach { item ->  // <5>
                        val json = item.toJSON()
                        val warehouse = Json.decodeFromString<WarehouseDao>(json).item
                        warehouses.add(warehouse)
                    }
                }
            } catch (e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext warehouses
        }
    }

    override suspend fun get(): List<Warehouse> {
        return withContext(Dispatchers.IO) {
            val locations = mutableListOf<Warehouse>()
            try {
                val db = databaseManager.warehouseDatabase
                db?.let {
                    val query = QueryBuilder
                        .select(SelectResult.all())
                        .from(DataSource.database(it).`as`(itemAliasName))
                        .where(Expression.property(typeAttributeName).equalTo(Expression.string(documentType)))
                    query.execute().allResults().forEach { item ->
                        val json = item.toJSON()
                        val warehouse = Json.decodeFromString<WarehouseDao>(json).item
                        locations.add(warehouse)
                    }
                }
            } catch (e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext locations
        }
    }

    override suspend fun warehouseCount(): Int {
        return withContext(Dispatchers.IO) {
            var resultCount = 0
            val countAliasName = "count"
            try {
                val db = databaseManager.warehouseDatabase
                db?.let {
                    val query = QueryBuilder  // <1>
                        .select(SelectResult.expression(Function.count(Expression.string("*"))).`as`(countAliasName)) // <2>
                        .from(DataSource.database(it)) // <3>
                        .where(Expression.property(typeAttributeName).equalTo(Expression.string(documentType))) // <4>
                    val results = query.execute().allResults()  // <5>
                    resultCount = results[0].getInt(countAliasName)  // <6>
                }
            } catch (e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext resultCount
        }
    }

    override val warehouseDatabaseName: () -> String? =  { databaseManager.warehouseDatabase?.name }

    override val warehouseDatabaseLocation: () -> String? = { databaseManager.warehouseDatabase?.path }
}