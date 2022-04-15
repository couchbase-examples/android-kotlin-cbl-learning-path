package com.couchbase.learningpath.data.location

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.couchbase.lite.Function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.models.Location
import com.couchbase.learningpath.models.LocationDTO

class LocationRepositoryDb(context: Context) : LocationRepository  {
    private val databaseResources: DatabaseManager = DatabaseManager.getInstance(context)
    private val locationType = "location"

    private val cityAttributeName = "city"
    private val countryAttributeName = "country"
    private val typeAttributeName = "type"
    private val itemAliasName = "item"

    override suspend fun getByCityCountry(searchCity: String, searchCountry: String?) : List<Location> {
        return withContext(Dispatchers.IO){
            val locations = mutableListOf<Location>()
            try {
                val db = databaseResources.locationDatabase
                db?.let { database ->
                    //search by city
                    var whereQueryExpression = Function
                        .lower(Expression.property(cityAttributeName)).like(Expression.string("%" + searchCity.lowercase() + "%")) // <1>

                    //search by optional country
                    searchCountry?.let { country ->
                        if (country.isNotEmpty()) {
                            val countryQueryExpression = Function
                                .lower(Expression.property(countryAttributeName))
                                .like(Expression.string("%" + country.lowercase() + "%"))  // <2>

                            whereQueryExpression =
                                whereQueryExpression.and(countryQueryExpression)  // <2>
                        }
                    }

                    //add type filter
                    val typeQueryExpression = Function
                        .lower(Expression.property(typeAttributeName))
                        .equalTo(Expression.string(locationType))  // <3>
                    whereQueryExpression = whereQueryExpression.and(typeQueryExpression) // <3>
                    //create query to execute using QueryBuilder API
                    val query = QueryBuilder
                        .select(SelectResult.all())
                        .from(DataSource.database(database).`as`(itemAliasName))
                        .where(whereQueryExpression) // <4>

                    //loop through results and add to list
                    query.execute().allResults().forEach { item ->
                        val json = item.toJSON()
                        val location = Json { ignoreUnknownKeys = true }.decodeFromString<LocationDTO>(json).item
                        locations.add(location)
                    }
                }
            } catch (e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext locations
        }
    }

    override suspend fun get(): List<Location> {
        return withContext(Dispatchers.IO) {
            val locations = mutableListOf<Location>()
            try {
                val db = databaseResources.locationDatabase
                db?.let {
                    val query = QueryBuilder
                        .select(SelectResult.all())
                        .from(DataSource.database(it).`as`(itemAliasName))
                        .where(Expression.property(typeAttributeName).equalTo(Expression.string(locationType)))
                    query.execute().allResults().forEach { item ->
                        val json = item.toJSON()
                        val location = Json{ ignoreUnknownKeys = true }.decodeFromString<LocationDTO>(json).item
                        locations.add(location)
                    }
                }
            } catch (e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext locations
        }
    }

    override suspend fun locationCount(): Int {
        return withContext(Dispatchers.IO) {
            var resultCount = 0
            val countAliasName = "count"
            try {
                val db = databaseResources.locationDatabase
                db?.let {
                    val query = QueryBuilder  // <1>
                        .select(SelectResult.expression(Function.count(Expression.string("*"))).`as`(countAliasName)) // <2>
                        .from(DataSource.database(it)) // <3>
                        .where(Expression.property(typeAttributeName).equalTo(Expression.string(locationType))) // <4>
                    val results = query.execute().allResults()  // <5>
                    resultCount = results[0].getInt(countAliasName)  // <6>
                }
            } catch (e: Exception){
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext resultCount
        }
    }

    override val locationDatabaseName : () -> String? =  { DatabaseManager.getInstance(context).locationDatabase?.name }

    override val locationDatabaseLocation: () -> String? = { DatabaseManager.getInstance(context).locationDatabase?.path }
}