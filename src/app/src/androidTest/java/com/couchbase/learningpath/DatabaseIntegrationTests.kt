package com.couchbase.learningpath

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.data.stockItem.StockItemRepositoryDb
import com.couchbase.learningpath.data.warehouse.WarehouseRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepositoryDb
import com.couchbase.learningpath.models.User
import com.couchbase.lite.CouchbaseLiteException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTests {

    lateinit var context: Context
    lateinit var databaseManager: DatabaseManager

    private val demoUser1Username = "demo@example.com"
    private val demoUser1Password = "P@ssw0rd12"
    private val demoUser1Team = "team1"

    private val demoUser2Username = "demo2@example.com"
    private val demoUser2Password = "P@ssw0rd12"
    private val demoUser2Team = "team2"

    @Before
    fun setup() {
        try {
            context = ApplicationProvider.getApplicationContext()
            databaseManager = DatabaseManager.getInstance(context)
        }catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    @Test
    fun databaseTest(){
        //arrange
        val user1 = User(username = demoUser1Username, password = demoUser1Password, team = demoUser1Team)
        val user2 = User(username = demoUser2Username, password = demoUser2Password, team = demoUser2Team)

        //act
        databaseManager.initializeDatabases(user1)

        //assert
        assertNotNull(databaseManager.inventoryDatabase)
        assertNotNull(databaseManager.warehouseDatabase)

        assertEquals("team1_inventory", databaseManager.inventoryDatabase?.name)
        assertEquals("warehouse", databaseManager.warehouseDatabase?.name)

        //arrange
        databaseManager.closeDatabases()
        databaseManager.initializeDatabases(user2)

        //assert
        assertEquals("team2_inventory", databaseManager.inventoryDatabase?.name)
        assertEquals("warehouse", databaseManager.warehouseDatabase?.name)
    }

    @Test
    suspend fun testWarehousePrebuiltDatabaseCount() {

        //arrange
        val user1 = User(username = demoUser1Username, password = demoUser1Password, team = demoUser1Team)
        databaseManager.initializeDatabases(user1)
        val warehouseRepository = WarehouseRepositoryDb(context)
        val stockItemRepository = StockItemRepositoryDb(context)


        //act
        val warehouseCount = warehouseRepository.warehouseCount()
        val stockItemCount = stockItemRepository.count()

        //assert
        assertEquals(12, warehouseCount)
        assertEquals(3000, stockItemCount)
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