package com.couchbase.learningpath.data.project

import android.util.Log
import com.couchbase.lite.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.*

import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.data.stockItem.StockItemRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepository
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.Warehouse
import com.couchbase.learningpath.models.Project
import com.couchbase.learningpath.models.ProjectDao
import com.couchbase.learningpath.services.AuthenticationService
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class ProjectRepositoryDb(
    private val authenticationService: AuthenticationService,
    private val warehouseRepository: WarehouseRepository,
    private val stockItemRepository: StockItemRepository,
    private val databaseManager: DatabaseManager
) : ProjectRepository {
    private val projectDocumentType = "project"
    private val auditDocumentType = "audit"

    override val databaseName: String
        get() = databaseManager.currentInventoryDatabaseName

    //live query demo of returning project documents

    override fun getDocuments(team: String): Flow<List<Project>> {
        try {
            val db = databaseManager.inventoryDatabase
            // NOTE - the as method is a also a keyword in Kotlin, so it must be escaped using
            // `as` - this will probably break intellisense, so it will act like the where
            // method isn't available  work around is to do your entire statement without the as
            // function call and add that in last
            db?.let { database ->
                val query = QueryBuilder        // <1>
                    .select(SelectResult.all()) // <2>
                    .from(DataSource.database(database).`as`("item")) // <3>
                    .where( //4
                        Expression.property("documentType").equalTo(Expression.string(projectDocumentType)) // <4>
                            .and(Expression.property("team").equalTo(Expression.string(team)))
                    ) // <4>

                // create a flow to return the results dynamically as needed - more information on
                // CoRoutine Flows can be found at
                // https://developer.android.com/kotlin/flow
                val flow = query        // <1>
                    .queryChangeFlow()  // <1>
                    .map { qc -> mapQueryChangeToProject(qc) } // <2>
                    .flowOn(Dispatchers.IO)  // <3>
                query.execute()  // <4>
                return flow  // <5>
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }
        return flow { }
    }

    private fun mapQueryChangeToProject(queryChange: QueryChange): List<Project> {
        val projects = mutableListOf<Project>() // 1
        queryChange.results?.let { results ->  // 2
            results.forEach { result ->      // 3
                val json = result.toJSON()     // 4
                val project =
                    Json.decodeFromString<ProjectDao>(json).item   // 5
                projects.add(project) // 6
            }
        }
        return projects // 7
    }

    override suspend fun get(documentId: String): Project {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseManager.inventoryDatabase
                db?.let { database ->
                    val doc = database.getDocument(documentId)
                    doc?.let { document ->
                        val json = document.toJSON()
                        json?.let { projectJson ->
                            return@withContext (
                                    Json.decodeFromString<Project>(projectJson))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }

            val team = authenticationService.getCurrentUser().team

            //calculate due date 90 days from today by default
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 90)
            val dueDate = SimpleDateFormat("MM-dd-yyyy", Locale.US)
                .parse(
                    "${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}-${
                        calendar.get(
                            Calendar.YEAR
                        )
                    }"
                )
            return@withContext (
                    Project(
                        projectId = documentId,
                        createdOn = Date(),
                        modifiedOn = Date(),
                        team = team,
                        dueDate = dueDate,
                        documentType = "project"
                    )
                    )
        }
    }

    override suspend fun updateProjectWarehouse(projectId: String, warehouse: Warehouse) {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseManager.inventoryDatabase
                val project = get(projectId)
                project.warehouse = warehouse
                db?.let { database ->
                    val json = Json.encodeToString(project)
                    val doc = MutableDocument(project.projectId, json)
                    database.save(doc)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun save(document: Project) {
        return withContext(Dispatchers.IO) {
            try {
                val db = databaseManager.inventoryDatabase
                db?.let { database ->
                    val json = Json.encodeToString(document)
                    val doc = MutableDocument(document.projectId, json)
                    database.save(doc)
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }

    override suspend fun completeProject(projectId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(documentId: String): Boolean {
        return withContext(Dispatchers.IO) {
            var result = false
            try {
                val db = databaseManager.inventoryDatabase
                db?.let { database ->
                    val projectDoc = database.getDocument(documentId)
                    projectDoc?.let { document ->
                        db.delete(document)
                        result = true
                    }
                }
            } catch (e: java.lang.Exception) {
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
                    val query = QueryBuilder  // 1
                        .select(
                            SelectResult.expression(com.couchbase.lite.Function.count(Expression.string("*")))
                                .`as`("count")
                        ) // 2
                        .from(DataSource.database(database)) //3
                        .where(
                            Expression.property("documentType").equalTo(Expression.string(projectDocumentType))
                        ) // 4
                    val results = query.execute().allResults() // 5
                    count = results[0].getInt("count") // 6
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
            return@withContext count
        }
    }

    override suspend fun loadSampleData() {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = authenticationService.getCurrentUser() // <1>
                val warehouses = warehouseRepository.get()  // <2>
                val warehouseCount = warehouses.count() - 1  // <3>
                val stockItems = stockItemRepository.get()   // <4>
                val stockItemsCount = stockItems.count() - 1 // <5>

                if (warehouseCount > 0 && stockItemsCount > 0) {
                    val db = databaseManager.inventoryDatabase
                    db?.let { database ->
                        // batch operations for saving multiple documents
                        // this is a faster way to process groups of documents at once
                        // https://docs.couchbase.com/couchbase-lite/current/android/document.html#batch-operations
                        database.inBatch(UnitOfWork {   // <1>
                            for (count in 0..11) {      // <2>
                                val projectId = UUID.randomUUID().toString()
                                val warehouse = warehouses[count] // <3>

                                val document = Project(  // <4>
                                    projectId = projectId,
                                    name = "${warehouse.name} Audit",
                                    description = "Audit of warehouse stock located in ${warehouse.city}, ${warehouse.state}.",
                                    isComplete = false,
                                    documentType = projectDocumentType,
                                    dueDate = SimpleDateFormat(
                                        "MM-dd-yyyy",
                                        Locale.US
                                    ).parse("${(1..12).random()}-${(1..27).random()}-${(2022..2024).random()}"),
                                    team = currentUser.team,
                                    createdBy = currentUser.username,
                                    modifiedBy = currentUser.username,
                                    createdOn = Date(),
                                    modifiedOn = Date(),
                                    warehouse = warehouses[count]
                                )
                                val json = Json.encodeToString(document) // <5>
                                val doc = MutableDocument(document.projectId, json) // <6>
                                database.save(doc) // <7>

                                //create random audit items per project // <8>
                                for (auditCount in 1..50){
                                    val stockItem = stockItems[(0..stockItemsCount).random()]
                                    val auditDocument = Audit(
                                        auditId = UUID.randomUUID().toString(),
                                        projectId = projectId,
                                        auditCount = (1..100000).random(),
                                        stockItem =  stockItem,
                                        documentType = auditDocumentType,
                                        notes = "Found item ${stockItem.name} - ${stockItem.description} in warehouse",
                                        team = currentUser.team,
                                        createdBy = currentUser.username,
                                        modifiedBy = currentUser.username,
                                        createdOn = Date(),
                                        modifiedOn = Date()
                                    )
                                    val auditJson = Json.encodeToString(auditDocument)
                                    val auditDoc = MutableDocument(auditDocument.auditId, auditJson)
                                    database.save(auditDoc)
                                }
                            }
                        })
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }
}