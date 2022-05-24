package com.couchbase.learningpath.data.audits

import android.content.Context
import android.util.Log
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.AuditDao
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.lite.*
import com.couchbase.lite.Function
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
            val team = authenticationService.getCurrentUser().team
            db?.let  { database ->
                val query = database.createQuery("SELECT * FROM _ AS item WHERE type=\"audit\" AND projectId=\$auditProjectId AND team=\$auditTeam") // 1

                val parameters = Parameters() // 3
                parameters.setValue("auditProjectId", projectId) // 3
                parameters.setValue("auditTeam", team) // 3
                query.parameters = parameters // 3

                val flow = query // 4
                    .queryChangeFlow() // 5
                    .map { qc -> mapQueryChangeToAudit(qc)} // 6
                    .flowOn(Dispatchers.IO) // 7
                query.execute() // 8
                return flow // 9
            }
        } catch (e: Exception){
            android.util.Log.e(e.message, e.stackTraceToString())
        }
        return null
    }

    override suspend fun get(projectId: String, auditId: String): Audit {
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
                type = "audit",
                createdOn = Date(),
                modifiedOn =  Date(),
                createdBy = user.username,
                modifiedBy = user.username,
                team = user.team)
        }
    }

    override suspend fun save(audit: Audit) {
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

    override suspend fun delete(auditId: String): Boolean {
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

    override suspend fun count(): Int {
        return withContext(Dispatchers.IO) {
            var count = 0
            try {
                val db = DatabaseManager.getInstance(context).inventoryDatabase
                db?.let { database ->
                    val query =  database.createQuery("SELECT COUNT(*) AS count FROM _ AS item WHERE type=\"audit\"") // 1
                    val results = query.execute().allResults() // 2
                    count = results[0].getInt("count") // 3
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext count
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