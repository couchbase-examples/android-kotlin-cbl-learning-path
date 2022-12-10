package com.couchbase.learningpath.data.userprofile

import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.MutableDocument

import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.data.KeyValueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserProfileRepository(
    private val databaseManager: DatabaseManager
) : KeyValueRepository {
    private val userProfileType = "user"

    override fun inventoryDatabaseName(): String {
        return databaseManager.currentInventoryDatabaseName
    }

    override fun inventoryDatabaseLocation(): String? {
        return databaseManager.inventoryDatabase?.path
    }

    override suspend fun get(currentUser: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {
            val results = HashMap<String, Any>()  //  <1>
            results["email"] = currentUser as Any  //  <2>

            val database = databaseManager.inventoryDatabase
            database?.let { db ->
                val documentId = getCurrentUserDocumentId(currentUser)
                val doc = db.getDocument(documentId)  //  <3>
                if (doc != null) {
                    if (doc.contains("givenName")) { //  <4>
                        results["givenName"] = doc.getString("givenName") as Any  //  <4>
                    }
                    if (doc.contains("surname")) { //  <4>
                        results["surname"] = doc.getString("surname") as Any  //  <4>
                    }
                    if (doc.contains("jobTitle")) { //  <4>
                        results["jobTitle"] = doc.getString("jobTitle") as Any  //  <4>
                    }
                    if (doc.contains("team")) { //  <4>
                        results["team"] = doc.getString("team") as Any  //  <4>
                    }
                    if (doc.contains("imageData")) { //  <4>
                        results["imageData"] = doc.getBlob("imageData") as Any  // <4>
                    }
                }
            }
            return@withContext results  //  <5>
        }
    }

    override suspend fun save(data: Map<String, Any>): Boolean {
        return withContext(Dispatchers.IO) {
            val email = data["email"] as String
            val documentId = getCurrentUserDocumentId(email)
            val mutableDocument = MutableDocument(documentId, data)
            try {
                val database = databaseManager.inventoryDatabase
                database?.save(mutableDocument)
            } catch (e: CouchbaseLiteException) {
                android.util.Log.e(e.message, e.stackTraceToString())
                return@withContext false
            }
            return@withContext true
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            val database = databaseManager.inventoryDatabase
            database?.let { db ->
                val query = "SELECT COUNT(*) AS count FROM _ WHERE documentType='$userProfileType'"
                val results = db.createQuery(query).execute().allResults()
                return@withContext results[0].getInt("count")
            }
            return@withContext 0
        }
    }

    suspend fun delete(documentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            val database = databaseManager.inventoryDatabase
            database?.let { db ->
                val document = db.getDocument(documentId)
                document?.let {
                    db.delete(it)
                    result = true
                }
            }
            return@withContext result
        }
    }

    private fun getCurrentUserDocumentId(currentUser: String): String {
        return "user::${currentUser}"
    }
}