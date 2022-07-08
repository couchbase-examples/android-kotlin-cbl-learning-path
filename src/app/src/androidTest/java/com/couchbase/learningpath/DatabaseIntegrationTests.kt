package com.couchbase.learningpath

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.data.audits.AuditRepositoryDb
import com.couchbase.learningpath.data.project.ProjectRepositoryDb
import com.couchbase.learningpath.data.stockItem.StockItemRepositoryDb
import com.couchbase.learningpath.data.userprofile.UserProfileRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepositoryDb
import com.couchbase.learningpath.models.Project
import com.couchbase.learningpath.models.User
import com.couchbase.learningpath.services.MockAuthenticationService
import com.couchbase.lite.CouchbaseLiteException
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseIntegrationTests {

    //setup for db manager
    private lateinit var context: Context
    private lateinit var databaseManager: DatabaseManager

    //setup for auth
    private lateinit var authenticationService: MockAuthenticationService

    //setup repositories
    private lateinit var projectRepository: ProjectRepositoryDb
    private lateinit var warehouseRepository: WarehouseRepositoryDb
    private lateinit var auditRepository: AuditRepositoryDb
    private lateinit var stockItemRepository: StockItemRepositoryDb
    private lateinit var userProfileRepository: UserProfileRepository

    //test users
    private lateinit var user1: User
    private lateinit var user2: User

    private val demoUser1Username = "demo@example.com"
    private val demoUser1Password = "P@ssw0rd12"
    private val demoUser1Team = "team1"

    private val demoUser2Username = "demo2@example.com"
    private val demoUser2Password = "P@ssw0rd12"
    private val demoUser2Team = "team2"

    @Before fun setup() {
        try {
            //arrange database
            context = ApplicationProvider.getApplicationContext()
            databaseManager = DatabaseManager.getInstance(context)

            //arrange test users
            user1 = User(username = demoUser1Username, password = demoUser1Password, team = demoUser1Team)
            user2 = User(username = demoUser2Username, password = demoUser2Password, team = demoUser2Team)

            //setup databases for use
            //if a test fails the database will be dirty, this
            //fixes that problem
            databaseManager.initializeDatabases(user1)
            databaseManager.deleteDatabases()
            databaseManager.initializeDatabases(user1)

            authenticationService = MockAuthenticationService()
            val isAuth = authenticationService.authenticatedUser(user1.username, user1.password)

            //arrange repositories
            auditRepository = AuditRepositoryDb(context, authenticationService)
            stockItemRepository = StockItemRepositoryDb(context)
            warehouseRepository = WarehouseRepositoryDb(context)
            userProfileRepository = UserProfileRepository(context)
            projectRepository = ProjectRepositoryDb(
                context = context,
                authenticationService = authenticationService,
                auditRepository = auditRepository,
                warehouseRepository = warehouseRepository,
                stockItemRepository = stockItemRepository)

            //load sample data
            runTest {
                projectRepository.loadSampleData()
            }
        }catch (e: Exception){
            Log.e(e.message, e.stackTraceToString())
        }
    }

    @After fun cleanUp() {
        try {
            databaseManager.closeDatabases()
            databaseManager.deleteDatabases()
        } catch (e: CouchbaseLiteException) {
            Log.e(e.message, e.stackTraceToString())
        }
    }

    @Test fun databaseSetupTest(){

        //arrange
        databaseManager.closeDatabases()
        databaseManager.initializeDatabases(user2)

        //assert
        assertEquals("team2_inventory", databaseManager.inventoryDatabase?.name)
        assertEquals("warehouse", databaseManager.warehouseDatabase?.name)

        //arrange
        databaseManager.deleteDatabases()
        databaseManager.initializeDatabases(user1)

        //assert
        assertNotNull(databaseManager.inventoryDatabase)
        assertNotNull(databaseManager.warehouseDatabase)

        assertEquals("team1_inventory", databaseManager.inventoryDatabase?.name)
        assertEquals("warehouse", databaseManager.warehouseDatabase?.name)
    }

    @Test
    fun testWarehousePrebuiltDatabaseCount() {
        //arrange
        runTest {
            //act
            val warehouseCount = warehouseRepository.warehouseCount()
            val stockItemCount = stockItemRepository.count()

            //assert
            assertEquals(50, warehouseCount)
            assertEquals(3000, stockItemCount)
        }
    }

    @Test fun testProjectCount(){
        //arrange
        runTest {
            //act
            val projectCount = projectRepository.count()

            //assert
            assertEquals(12, projectCount)
        }
    }

    @Test fun testGetProjectDocuments() {
        //arrange
        runTest {
            //act
            val collectJob = launch(UnconfinedTestDispatcher()) {
                val flow = projectRepository.getDocuments(user1.team)
                flow.collect { projects ->
                    //assert
                    assertEquals(12, projects.count())
                }
            }
            collectJob.cancel()
        }

        runTest {
                //act
            val collectJob = launch(UnconfinedTestDispatcher()) {
                val projectTeam2 = projectRepository.getDocuments(user2 .team)
                    .collect { projects ->
                //assert
                    assertEquals(0, projects.count())
                }
            }
            collectJob.cancel()
        }
    }

    @Test fun testGetProject(){
        //arrange
        runTest{
            val collectJob = launch(UnconfinedTestDispatcher()) {
                projectRepository.getDocuments(user1 .team)
                    .collect { projects ->
                        val firstProject = projects[0]
                        //act
                        val project = projectRepository.get(firstProject.projectId)

                        //assert
                        assertNotNull(project)
                        assertEquals(firstProject.name, project.name)
                    }
            }
            collectJob.cancel()
        }
    }

    @Test fun testUpdateProjectWarehouse(){
        //arrange

        //act

        //assert
        throw Exception("Not Implemented")

    }

    @Test fun testProjectSave(){
        //arrange

        //act


        //assert
        throw Exception("Not Implemented")
    }

    @Test fun testProjectDelete(){
        //arrange

        //act


        //assert
        throw Exception("Not Implemented")
    }



}