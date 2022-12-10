package com.couchbase.learningpath.data

import android.content.Context
import com.couchbase.learningpath.models.User
import com.couchbase.lite.*
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class DatabaseManager(private val context: Context) {

    var inventoryDatabase: Database? = null
    var warehouseDatabase: Database? = null

    private val defaultInventoryDatabaseName = "inventory"
    private val warehouseDatabaseName = "warehouse"
    private val startingWarehouseFileName = "startingWarehouses.zip"
    private val startingWarehouseDatabaseName = "startingWarehouses"

    private val documentTypeIndexName = "idxDocumentType"
    private val documentTypeAttributeName = "documentType"

    private val teamIndexName = "idxTeam"
    private val teamAttributeName = "team"

    private val cityIndexName = "idxCityType"
    private val cityAttributeName = "city"

    private val cityStateIndexName = "idxCityStateType"
    private val stateAttributeName = "state"

    private val auditIndexName = "idxAudit"
    private val projectIdAttributeName = "projectId"

    var currentInventoryDatabaseName = "inventory"

    init {
        //setup couchbase lite
        CouchbaseLite.init(context)

        //turn on uber logging - in production apps this shouldn't be turn on
        Database.log.console.domains = LogDomain.ALL_DOMAINS
        Database.log.console.level = LogLevel.VERBOSE
    }

    fun dispose() {
        inventoryDatabase?.close()
        warehouseDatabase?.close()
    }

    fun deleteDatabases() {
        try {
            closeDatabases()
            Database.delete(currentInventoryDatabaseName, context.filesDir)
            Database.delete(warehouseDatabaseName, context.filesDir)
        } catch (e: Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    fun closeDatabases() {
        try {
            inventoryDatabase?.close()
            warehouseDatabase?.close()
        } catch (e: java.lang.Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    fun initializeDatabases(
        currentUser: User
    ) {
        try {
            val dbConfig = DatabaseConfigurationFactory.create(context.filesDir.toString())

            // create or open a database to share between team members to store
            // projects, assets, and user profiles
            // calculate database name based on current logged in users team name
            val teamName = (currentUser.team.filterNot { it.isWhitespace() }).lowercase()
            currentInventoryDatabaseName = teamName.plus("_").plus(defaultInventoryDatabaseName)
            inventoryDatabase = Database(currentInventoryDatabaseName, dbConfig)

            //setup the warehouse Database
            setupWarehouseDatabase(dbConfig)

            //create indexes for database queries
            createTypeIndex(warehouseDatabase)
            createTypeIndex(inventoryDatabase)

            createTeamTypeIndex()
            createCityTypeIndex()
            createCityCountryTypeIndex()
            createAuditIndex()

        } catch (e: Exception) {
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    private fun setupWarehouseDatabase(dbConfig: DatabaseConfiguration) {
        // create the warehouse database if it doesn't already exist
        if (!Database.exists(warehouseDatabaseName, context.filesDir)) {
            unzip(startingWarehouseFileName, File(context.filesDir.toString()))

            // copy the warehouse database to the project database
            // never open the database directly as this will cause issues
            // with sync
            val warehouseDbFile =
                File(
                    String.format(
                        "%s/%s",
                        context.filesDir,
                        ("${startingWarehouseDatabaseName}.cblite2")
                    )
                )
            Database.copy(warehouseDbFile, warehouseDatabaseName, dbConfig)
        }
        warehouseDatabase = Database(warehouseDatabaseName, dbConfig)
    }

    private fun createTeamTypeIndex(){
        try {
            inventoryDatabase?.let {  // 1
                if (!it.indexes.contains(teamIndexName)) {
                    // create index for ProjectListView to only return documents with
                    // the type attribute set to project and the team attribute set to the
                    // logged in users team
                    it.createIndex( // 2
                        teamIndexName, // 3
                        IndexBuilder.valueIndex(   // 4
                            ValueIndexItem.property(documentTypeAttributeName), // 5
                            ValueIndexItem.property(teamAttributeName)) // 5
                    )
                }
            }
        } catch (e: Exception){
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    private fun createCityTypeIndex(){
        try {
            inventoryDatabase?.let {  // 1
                if (!it.indexes.contains(cityIndexName)) {
                    // create index for Warehouse only return documents with
                    // the type attribute set to warehouse and the city attribute filtered
                    // by value sent in using `like` statement
                    it.createIndex( // 3
                        cityIndexName, // 4
                        IndexBuilder.valueIndex(   // 5
                            ValueIndexItem.property(documentTypeAttributeName), // 5
                            ValueIndexItem.property(cityAttributeName)) // 5
                    )
                }
            }
        } catch (e: Exception){
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    private fun createCityCountryTypeIndex(){
        try {
            inventoryDatabase?.let {  // 1
                if (!it.indexes.contains(cityIndexName)) {
                    // create index for Locations only return documents with
                    // the type attribute set to location, the city attribute filtered
                    // by value sent in using `like` statement, and the country attribute filtered
                    // by the value sent in using `like` statement

                    it.createIndex( // 3
                        cityStateIndexName, // 4
                        IndexBuilder.valueIndex(   // 5
                            ValueIndexItem.property(documentTypeAttributeName), // 5
                            ValueIndexItem.property(cityAttributeName), // 5
                            ValueIndexItem.property(stateAttributeName)) // 5
                    )
                }
            }
        } catch (e: Exception){
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    private fun createAuditIndex(){
        try {
            inventoryDatabase?.let {  // 1
                if (!it.indexes.contains(auditIndexName)) {
                    // create index for Audits to return documents with
                    // the type attribute set to audit, the projectId filtered
                    // by value sent in using equals, and the team attribute filtered
                    // by the value sent in using equals

                    it.createIndex( // 3
                        auditIndexName, // 4
                        IndexBuilder.valueIndex(   // 5
                            ValueIndexItem.property(documentTypeAttributeName), // 5
                            ValueIndexItem.property(projectIdAttributeName), // 5
                            ValueIndexItem.property(teamAttributeName)) // 5
                    )
                }
            }
        } catch (e: Exception){
            android.util.Log.e(e.message, e.stackTraceToString())
        }
    }

    private fun createTypeIndex(
        database: Database?
    ) {
        // create indexes for document type
        // create index for document type if it doesn't exist
        database?.let {
            if (!it.indexes.contains(documentTypeIndexName)) {
                it.createIndex(
                    documentTypeIndexName, IndexBuilder.valueIndex(
                        ValueIndexItem.expression(
                            Expression.property(documentTypeAttributeName)
                        )
                    )
                )
            }
        }
    }

    private fun unzip(
        file: String,
        destination: File
    ) {
        context.assets.open(file).use { stream ->
            val buffer = ByteArray(1024)
            val zis = ZipInputStream(stream)
            var ze: ZipEntry? = zis.nextEntry
            while (ze != null) {
                val fileName: String = ze.name
                val newFile = File(destination, fileName)
                if (ze.isDirectory) {
                    newFile.mkdirs()
                } else {
                    File(newFile.parent!!).mkdirs()
                    val fos = FileOutputStream(newFile)
                    var len: Int
                    while (zis.read(buffer).also { len = it } > 0) {
                        fos.write(buffer, 0, len)
                    }
                    fos.close()
                }
                ze = zis.nextEntry
            }
            zis.closeEntry()
            zis.close()
            stream.close()
        }
    }
}