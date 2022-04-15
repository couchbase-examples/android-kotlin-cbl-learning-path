package com.couchbase.learningpath.data.location

import com.couchbase.learningpath.models.Location

interface LocationRepository {

    val locationDatabaseName: () -> String?

    val locationDatabaseLocation:() -> String?

    suspend fun getByCityCountry(
        searchCity: String,
        searchCountry: String?): List<Location>

    suspend fun get(): List<Location>

    suspend fun locationCount(): Int
}