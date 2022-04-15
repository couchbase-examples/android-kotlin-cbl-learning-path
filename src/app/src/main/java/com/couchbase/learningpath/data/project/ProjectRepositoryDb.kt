package com.couchbase.learningpath.data.project

import android.content.Context
import android.util.Log
import com.couchbase.lite.*
import com.couchbase.lite.Function
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.data.location.LocationRepository
import com.couchbase.learningpath.models.Location
import com.couchbase.learningpath.models.Project
import com.couchbase.learningpath.models.ProjectDTO
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.services.RandomDescriptionService


class ProjectRepositoryDb(
    private val context: Context,
    private val authenticationService: AuthenticationService,
    private val locationRepository: LocationRepository
) : ProjectRepository {
    private val projectType = "project"

    override val databaseName: String
        get() = DatabaseManager.getInstance(context).currentInventoryDatabaseName

    //live query demo of returning project documents
    override fun getDocuments(team: String): Flow<List<Project>> {
        try {
            val db = DatabaseManager.getInstance(context).inventoryDatabase
            // NOTE - the as method is a also a keyword in Kotlin, so it must be escaped using
            // `as` - this will probably break intellisense, so it will act like the where method isn't available
            // work around is to do your entire statement without the as function call and add that in last
            db?.let { database ->
                val query = QueryBuilder        // 1
                    .select(SelectResult.all()) // 2
                    .from(DataSource.database(database).`as`("item")) // 3
                    .where(
                        Expression.property("type").equalTo(Expression.string(projectType)) // 4
                            .and(Expression.property("team").equalTo(Expression.string(team)))
                    ) //4

                // create a flow to return the results dynamically as needed - more information on CoRoutine Flows can be found at
                // https://developer.android.com/kotlin/flow
                val flow = query        // 1
                    .queryChangeFlow()  // 2
                    .map { qc -> mapQueryChangeToProject(qc) } // 3
                    .flowOn(Dispatchers.IO)  // 4
                query.execute()  // 5
                return flow  // 6
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }
        return flow { }
    }

    private fun mapQueryChangeToProject(queryChange: QueryChange): List<Project> {
        val projects = mutableListOf<Project>() // 1
        queryChange.results?.let { results ->  // 2
            results.forEach() { result ->      // 3
                val json = result.toJSON()     // 4
                val project =
                    Json { ignoreUnknownKeys = true }.decodeFromString<ProjectDTO>(json).item   // 5
                projects.add(project) // 6
            }
        }
        return projects // 7
    }

    override suspend fun get(documentId: String): Project {
        return withContext(Dispatchers.IO) {
            try {
                val db = DatabaseManager.getInstance(context).inventoryDatabase
                db?.let { database ->
                    val doc = database.getDocument(documentId)
                    doc?.let { document ->
                        val json = document.toJSON()
                        json?.let { projectJson ->
                            return@withContext (
                                    Json { ignoreUnknownKeys = true }.decodeFromString<Project>(
                                        projectJson
                                    ))
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }

            val team = authenticationService.getCurrentUser()?.team ?: ""

            //calculate due date 90 days from today by default
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 90)
            val dueDate = SimpleDateFormat("MM-dd-yyyy", Locale.US)
                .parse("${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(
                    Calendar.YEAR)}")
            return@withContext (
                    Project(
                        projectId = documentId,
                        createdOn = Date(),
                        modifiedOn = Date(),
                        team = team,
                        dueDate = dueDate,
                        type = "project"
                    )
                    )
        }
    }

    override suspend fun updateProjectLocation(projectId: String, location: Location) {
        return withContext(Dispatchers.IO) {
            try {
                val db = DatabaseManager.getInstance(context).inventoryDatabase
                val project = get(projectId)
                project.location = location
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
                val db = DatabaseManager.getInstance(context).inventoryDatabase
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
                val db = DatabaseManager.getInstance(context).inventoryDatabase
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
                val db = DatabaseManager.getInstance(context).inventoryDatabase
                db?.let { database ->
                    val query = QueryBuilder  // 1
                        .select(
                            SelectResult.expression(Function.count(Expression.string("*")))
                                .`as`("count")
                        ) // 2
                        .from(DataSource.database(database)) //3
                        .where(
                            Expression.property("type").equalTo(Expression.string(projectType))
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
                authenticationService.getCurrentUser()?.let {
                    val descriptionService = RandomDescriptionService()
                    val locations = locationRepository.get()
                    val locationsCount = locations.count() - 1
                    if (locationsCount > 0) {
                        val db = DatabaseManager.getInstance(context).inventoryDatabase
                        db?.let { database ->
                            // batch operations for saving multiple documents
                            // this is a faster way to process groups of documents at once
                            // https://docs.couchbase.com/couchbase-lite/current/android/document.html#batch-operations
                            database.inBatch(UnitOfWork {   // 1
                                for (count in 1..10) {      // 2
                                    val document = Project(  //3
                                        projectId = UUID.randomUUID().toString(),
                                        name = "Audit ${(1..1000000).random()}",
                                        description = descriptionService.randomDescription(),
                                        isComplete = false,
                                        type = projectType,
                                        dueDate = SimpleDateFormat(
                                            "MM-dd-yyyy",
                                            Locale.US
                                        ).parse("${(1..12).random()}-${(1..27).random()}-${(2022..2024).random()}"),
                                        team = it.team,
                                        createdBy = it.username,
                                        modifiedBy = it.username,
                                        createdOn = Date(),
                                        modifiedOn = Date(),
                                        location = locations[(0..locationsCount).random()]
                                    )
                                    val json = Json.encodeToString(document) // 4
                                    val doc = MutableDocument(document.projectId, json) // 5
                                    database.save(doc) // 6
                                }
                            })
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(e.message, e.stackTraceToString())
            }
        }
    }
}