package com.couchbase.learningpath.data.audits

import android.content.Context
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.AuditDao
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.QueryChange
import com.couchbase.lite.queryChangeFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuditRepositoryDb(
    var context: Context,
    private val authenticationService: AuthenticationService
) : AuditRepository {

    private val databaseResources: DatabaseManager = DatabaseManager.getInstance(context)

    override fun getAuditsByProjectId(projectId: String): Flow<List<Audit>>? {
        try {
            val db = databaseResources.inventoryDatabase
            db?.let  { database ->
                val query = database.createQuery("SELECT * FROM _ AS item WHERE type=\"audit\" AND projectId=\"$projectId\"")
                val flow = query
                    .queryChangeFlow()
                    .map { qc -> mapQueryChangeToAudit(qc)}
                    .flowOn(Dispatchers.IO)
                query.execute()
                return flow
            }
        } catch (e: Exception){
            android.util.Log.e(e.message, e.stackTraceToString())
        }
        return null
    }

    override suspend fun getAudit(projectId: String, auditId: String): Audit {
        return withContext(Dispatchers.IO){
            try {
                val db = databaseResources.inventoryDatabase
                db?.let { database ->
                    val doc = database.getDocument(auditId)
                    doc?.let { document  ->
                        val json = document.toJSON()
                        json?.let {
                            return@withContext Json.decodeFromString<Audit>(it)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(e.message, e.stackTraceToString())
            }
            val user = authenticationService.getCurrentUser()
            return@withContext Audit(
                projectId = projectId,
                auditId = UUID.randomUUID().toString(),
                createdOn = Date(),
                modifiedOn =  Date(),
                createdBy = user.username,
                modifiedBy = user.username,
                team = user.team)
        }
    }

    override suspend fun saveAudit(audit: Audit) {
        return withContext(Dispatchers.IO){
            try {
                val db = databaseResources.inventoryDatabase
                db?.let { database ->
                    val json = Json.encodeToString(audit)
                    val doc = MutableDocument(audit.auditId, json)
                    database.save(doc)
                }
            }catch(e: Exception){
                android.util.Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun deleteAudit(auditId: String): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val db = databaseResources.inventoryDatabase
                db?.let { database ->
                    val doc = database.getDocument(auditId)
                    doc?.let { document ->
                        db.delete(document)
                        result = true
                    }
                }
            } catch (e: Exception){
                android.util.Log.e(e.message, e.stackTraceToString())
            }
            return@withContext result
        }
    }

    private fun mapQueryChangeToAudit (queryChange: QueryChange) : List<Audit> {
        val audits = mutableListOf<Audit>()
        queryChange.results?.let { results ->
            results.forEach(){ result ->
                val audit = Json.decodeFromString<AuditDao>(result.toJSON()).item
                audits.add(audit)
            }
        }
        return audits
    }
}