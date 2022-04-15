package com.couchbase.learningpath.data

interface KeyValueRepository {

    fun inventoryDatabaseName(): String
    fun inventoryDatabaseLocation(): String?

    suspend fun count(): Int
    suspend fun get(currentUser: String): Map<String, Any>
    suspend fun save(data: Map<String, Any>) : Boolean
}