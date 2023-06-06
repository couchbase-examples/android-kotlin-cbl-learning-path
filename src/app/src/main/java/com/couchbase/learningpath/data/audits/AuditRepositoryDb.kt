@file:OptIn(ExperimentalSerializationApi::class)

package com.couchbase.learningpath.data.audits

import android.util.Log
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.AuditDao
import com.couchbase.learningpath.models.StockItem
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.lite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class AuditRepositoryDb(
    private val authenticationService: AuthenticationService,
    private val databaseManager: DatabaseManager
) : AuditRepository {

    override fun getAuditsByProjectId(projectId: String): Flow<List<Audit>>? {
        try {
            val db = databaseManager.inventoryDatabase
            val team = authenticationService.getCurrentUser().team
            db?.let { database ->
                val query =
                    database.createQuery("SELECT * FROM _ AS item WHERE documentType=\"audit\" AND projectId=\$auditProjectId AND team=\$auditTeam") // 1

                val parameters = Parameters() // 2
                parameters.setValue("auditProjectId", projectId) // 2
                parameters.setValue("auditTeam", team) // 2
                query.parameters = parameters // 3

                val flow = query // 4
                    .queryChangeFlow() // 5
                    .map { qc -> mapQueryChangeToAudit(qc) } // 6
                    .flowOn(Dispatchers.IO) // 7
                query.execute() // 8
                return flow // 9
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }
        return null
    }

    override suspend fun get(projectId: String, auditId: String): Audit {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseManager.inventoryDatabase
                db?.let { database ->
                    val doc = database.getDocument(auditId)
                    doc?.let { document ->
                        val json = document.toJSON()
                        json?.let {
                            return@withContext Json.decodeFromString<Audit>(it)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            val user = authenticationService.getCurrentUser()
            return@withContext Audit(
                projectId = projectId,
                auditId = auditId,
                stockItem = null,
                documentType = "audit",
                createdOn = Date(),
                modifiedOn = Date(),
                createdBy = user.username,
                modifiedBy = user.username,
                team = user.team
            )
        }
    }

    override suspend fun save(document: Audit) {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseManager.inventoryDatabase
                db?.let { database ->
                    val json = Json.encodeToString(document)
                    val doc = MutableDocument(document.auditId, json)
                    database.save(doc)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun updateAuditStockItem(
        projectId: String,
        auditId: String,
        stockItem: StockItem
    ) {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseManager.inventoryDatabase
                val audit = get(projectId, auditId)
                audit.stockItem = stockItem
                audit.notes = "Found item ${stockItem.name} - ${stockItem.description} in warehouse"
                db?.let { database ->
                    val json = Json.encodeToString(audit)
                    val doc = MutableDocument(audit.auditId, json)
                    database.save(doc)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun deleteProjectAudits(projectId: String) {
        return withContext(Dispatchers.IO) {
            val flow = getAuditsByProjectId(projectId)
            flow?.let { f ->
                val items =  f.single()
                items.forEach { item ->
                    delete(item.auditId)
                }
            }
        }
    }

    override suspend fun delete(documentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val db = databaseManager.inventoryDatabase
                db?.let { database ->
                    val doc = database.getDocument(documentId)
                    doc?.let { document ->
                        db.delete(document)
                        result = true
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext result
        }
    }

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            try {
                val db = databaseManager.inventoryDatabase
                db?.let { database ->
                    val query =
                        database.createQuery("SELECT COUNT(*) AS count FROM _ AS item WHERE documentType=\"audit\"") // 1
                    val results = query.execute().allResults() // 2
                    count = results[0].getInt("count") // 3
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext count
        }
    }

    private fun mapQueryChangeToAudit(queryChange: QueryChange): List<Audit> {
        val audits = mutableListOf<Audit>()
        queryChange.results?.let { results ->
            results.forEach { result ->
                val audit = Json.decodeFromString<AuditDao>(result.toJSON()).item
                audits.add(audit)
            }
        }
        return audits
    }
}