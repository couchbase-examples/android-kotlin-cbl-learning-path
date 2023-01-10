package com.couchbase.learningpath.data.userprofile

import com.couchbase.learningpath.data.KeyValueRepository

interface UserProfileRepository : KeyValueRepository {

    fun inventoryDatabaseName(): String
    fun inventoryDatabaseLocation(): String?
    suspend fun delete(documentId: String): Boolean
}