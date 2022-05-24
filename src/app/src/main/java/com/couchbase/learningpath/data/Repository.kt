package com.couchbase.learningpath.data

interface Repository<T> {

    suspend fun save(document: T)
    suspend fun delete(documentId: String): Boolean
    suspend fun count(): Int

}