package com.couchbase.learningpath

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.data.stockItem.StockItemRepositoryDb
import com.couchbase.learningpath.data.warehouse.WarehouseRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepositoryDb
import com.couchbase.learningpath.models.Audit
import com.couchbase.learningpath.models.StockItem
import com.couchbase.learningpath.models.User
import com.couchbase.lite.CouchbaseLiteException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StockItemsIntegrationTests {

    lateinit var context: Context
    lateinit var databaseManager: DatabaseManager

    private val demoUser1Username = "demo@example.com"
    private val demoUser1Password = "P@ssw0rd12"
    private val demoUser1Team = "team1"

    @Before
    fun setup() {
        try {
            context = ApplicationProvider.getApplicationContext()
            databaseManager = DatabaseManager.getInstance(context)
            val user1 = User(username = demoUser1Username, password = demoUser1Password, team = demoUser1Team)
            databaseManager.initializeDatabases(user1)

        }catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    @Test
    fun testAuditGet() {

        //arrange

        //act
        runTest {
            throw Exception("Not Implemented")
        }
        //assert
    }

    @Test
    fun testGetAuditsByProjectId(){
        //arrange

        //act


        //assert
      throw Exception("Not Implemented")
    }

    @Test
    fun testUpdateAuditStockItem(){
        //arrange

        //act


        //assert
        throw Exception("Not Implemented")

    }

    @Test
    fun testDeleteProjectAudits(){
        //arrange

        //act


        //assert
        throw Exception("Not Implemented")
    }

    @After
    fun cleanUp() {
        try {
            databaseManager.closeDatabases()
            databaseManager.deleteDatabases()
        } catch (e: CouchbaseLiteException) {
            Log.e(e.message, e.stackTraceToString())
        }
    }
}