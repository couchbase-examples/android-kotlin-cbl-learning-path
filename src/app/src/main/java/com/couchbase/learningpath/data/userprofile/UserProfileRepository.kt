package com.couchbase.learningpath.data.userprofile

import android.content.Context
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.MutableDocument

import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.data.KeyValueRepository

class UserProfileRepository(var context: Context) : KeyValueRepository {
    private val userProfileType = "user"

    override fun inventoryDatabaseName(): String {
        return DatabaseManager.getInstance(context).currentInventoryDatabaseName
    }

    override fun inventoryDatabaseLocation(): String? {
        return DatabaseManager.getInstance(context).inventoryDatabase?.path
    }

    override suspend fun get(currentUser: String): Map<String, Any> {
        val results = HashMap<String, Any>()  //  <1>
        results["email"] = currentUser as Any  //  <2>

        val database = DatabaseManager.getInstance(context).inventoryDatabase
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
        return results  //  <5>
    }

    override suspend fun save(data: Map<String, Any>): Boolean {
        val email = data["email"] as String
        val documentId = getCurrentUserDocumentId(email)
        val mutableDocument = MutableDocument(documentId, data)
        try {
            val database = DatabaseManager.getInstance(context).inventoryDatabase
            database?.save(mutableDocument)
        } catch (e: CouchbaseLiteException) {
            android.util.Log.e(e.message, e.stackTraceToString())
            return false
        }
        return true
    }

    override suspend fun count(): Int {
        val database = DatabaseManager.getInstance(context).inventoryDatabase
        database?.let { db ->
            val query = "SELECT COUNT(*) AS count FROM _ WHERE type='$userProfileType'"
            val results = db.createQuery(query).execute().allResults()
            return results[0].getInt("count")
        }
        return 0
    }

    private fun getCurrentUserDocumentId(currentUser: String): String {
        return "user::${currentUser}"
    }
}