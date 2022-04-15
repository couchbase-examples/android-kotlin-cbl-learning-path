package com.couchbase.learningpath.data

interface Repository<T> {
    val databaseName: String
    suspend fun save(document: T)
    suspend fun get(documentId: String): T
    suspend fun delete(documentId: String): Boolean
    suspend fun count(): Int
    suspend fun loadSampleData()
}