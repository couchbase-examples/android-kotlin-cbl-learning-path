package com.couchbase.learningpath.data

interface KeyValueRepository {

    suspend fun count(): Int
    suspend fun get(key: String): Map<String, Any?>
    suspend fun save(data: Map<String, Any>) : Boolean
}