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
import com.couchbase.learningpath.data.warehouse.WarehouseRepositoryDb
import com.couchbase.learningpath.models.*
import com.couchbase.learningpath.services.MockAuthenticationService
import com.couchbase.lite.CouchbaseLiteException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
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
    private val demoUser1Surname = "Doe"
    private val demoUser1GivenName = "Jane"
    private val demoUser1JobTitle = "Software Developer"

    private val demoUser2Username = "demo2@example.com"
    private val demoUser2Password = "P@ssw0rd12"
    private val demoUser2Team = "team2"
    private val demoUser2Surname = "Smith"
    private val demoUser2GivenName = "Bob"
    private val demoUser2JobTitle = "QA Engineer"

    @Before
    fun setup() {
        try {
            //arrange database
            context = ApplicationProvider.getApplicationContext()
            databaseManager = DatabaseManager(context)

            //arrange test users
            user1 = User(
                username = demoUser1Username,
                password = demoUser1Password,
                team = demoUser1Team
            )
            user2 = User(
                username = demoUser2Username,
                password = demoUser2Password,
                team = demoUser2Team
            )

            //setup databases for use
            //if a test fails the database will be dirty, this
            //fixes that problem
            databaseManager.initializeDatabases(user1)
            databaseManager.deleteDatabases()
            databaseManager.initializeDatabases(user1)

            authenticationService = MockAuthenticationService()
            val isAuth = authenticationService.authenticatedUser(user1.username, user1.password)

            //arrange repositories
            auditRepository = AuditRepositoryDb(authenticationService, databaseManager)
            stockItemRepository = StockItemRepositoryDb(databaseManager)
            warehouseRepository = WarehouseRepositoryDb(databaseManager)
            userProfileRepository = UserProfileRepository(databaseManager)
            projectRepository = ProjectRepositoryDb(
                authenticationService = authenticationService,
                warehouseRepository = warehouseRepository,
                stockItemRepository = stockItemRepository,
                databaseManager = databaseManager
            )

            //load sample data
            runTest {
                projectRepository.loadSampleData()
            }
        } catch (e: Exception) {
            Log.e(e.message, e.stackTraceToString())
        }
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

    @Test
    fun databaseSetupTest() {
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
    fun testUserProfileCount() {
        //arrange
        runTest {
            val dict = getDemoUser1ProfileDictionary()

            //act
            val preCount = userProfileRepository.count()
            val didSave = userProfileRepository.save(dict)
            val postCount = userProfileRepository.count()

            //assert
            assertTrue(didSave)
            assertEquals(0, preCount)
            assertEquals(1, postCount)

            userProfileRepository.delete("user::${user1.username}")
        }
    }

    @Test
    fun testSaveGetUserProfile() {
        //arrange
        runTest {
            val dict = getDemoUser1ProfileDictionary()

            //act
            userProfileRepository.save(dict)
            val demoUser1UserProfile = userProfileRepository.get(user1.username)

            //assert
            assertEquals(demoUser1GivenName, demoUser1UserProfile["givenName"])
            assertEquals(demoUser1Surname, demoUser1UserProfile["surname"])
            assertEquals(demoUser1JobTitle, demoUser1UserProfile["jobTitle"])
            assertEquals(user1.username, demoUser1UserProfile["email"])

            //cleanup
            userProfileRepository.delete("user::${user1.username}")
        }
    }

    @Test
    fun testProjectCount() {
        //arrange
        runTest {
            //act
            val projectCount = projectRepository.count()

            //assert
            assertEquals(12, projectCount)
        }
    }

    @Test
    fun testGetProjectDocuments() {
        //arrange
        runTest {
            //act
            val projects = projectRepository.getDocuments(user1.team).first()
            val projectsTeam2 = projectRepository.getDocuments(user2.team).first()

            //assert
            assertEquals(12, projects.count())
            assertEquals(0, projectsTeam2.count())
        }
    }

    @Test
    fun testGetProject() {
        //arrange
        runTest {
            val projects = projectRepository.getDocuments(user1.team).first()
            val firstProject = projects.first()
            //act
            val project = projectRepository.get(firstProject.projectId)

            //assert
            assertNotNull(project)
            assertEquals(firstProject.name, project.name)
        }
    }

    @Test
    fun testUpdateProjectWarehouse() {
        //arrange
        runTest {
            val projects = projectRepository.getDocuments(user1.team).first()
            val secondProject = projects[1]
            val warehouses = warehouseRepository.get()
            val warehouse = warehouses[warehouses.count() - 2]
            //act
            projectRepository.updateProjectWarehouse(secondProject.projectId, warehouse)
            val updatedProject = projectRepository.get(secondProject.projectId)

            //assert
            assertNotNull(updatedProject)
            assertEquals(updatedProject.warehouse, warehouse)
            assertNotEquals(secondProject.warehouse, warehouse)
        }
    }

    @Test
    fun testProjectSave() {
        //arrange
        runTest {
            val projects = projectRepository.getDocuments(user1.team).first()
            val thirdProject = projects[3]
            thirdProject.name = "Updated Name"
            thirdProject.description = "Updated Description"
            //act
            projectRepository.save(thirdProject)
            val updatedProject = projectRepository.get(thirdProject.projectId)

            //assert
            assertNotNull(updatedProject)
            assertEquals("Updated Name", updatedProject.name)
            assertEquals("Updated Description", updatedProject.description)
        }
    }

    @Test
    fun testProjectDelete() {
        runTest {
            val projects = projectRepository.getDocuments(user1.team).first()
            val deleteProject = projects[projects.count() - 1]
            //act
            val result = projectRepository.delete(deleteProject.projectId)
            val updatedProject = projectRepository.get(deleteProject.projectId)

            //assert
            assertTrue(result)
            assertNotNull(updatedProject)
            assertNotEquals(deleteProject.name, updatedProject.name)
        }
    }

    @Test
    fun testWarehousePrebuiltDatabaseCount() {
        //arrange
        runTest {
            //act
            val warehouseCount = warehouseRepository.warehouseCount()
            val stockItemCount = stockItemRepository.count()

            //assert
            assertEquals(55, warehouseCount)
            assertEquals(3000, stockItemCount)
        }
    }

    @Test
    fun testGetWarehouse() {
        //arrange
        runTest {
            //act
            val warehouses = warehouseRepository.get()

            //assert
            assertNotNull(warehouses)
            assertEquals(55, warehouses.count())
        }
    }

    @Test
    fun testWarehouseGetByCityState() {
        //arrange
        runTest {
            //act
            val warehouses = warehouseRepository.get()
            val warehouse1 = warehouses[0]
            val warehouse1CitySearch = warehouse1.city.substring(0, 3)
            val warehouse2 = warehouses[1]
            val warehouse2CitySearch = warehouse2.city.substring(0, 3)
            val warehouse2StateSearch = warehouse2.state.substring(0, 1)

            val warehouseResults1 = warehouseRepository.getByCityState(warehouse1CitySearch, null)
            val warehouseResults2 =
                warehouseRepository.getByCityState(warehouse2CitySearch, warehouse2StateSearch)

            //assert
            assertNotNull(warehouseResults1)
            assertEquals(2, warehouseResults1.count())
            assertEquals(warehouse1.city, warehouseResults1.first().city)

            assertNotNull(warehouseResults2)
            assertEquals(1, warehouseResults2.count())
            assertEquals(warehouse2.city, warehouseResults2.first().city)
            assertEquals(warehouse2.state, warehouseResults2.first().state)
        }
    }

    @Test
    fun testGetAuditsByProjectId() {
        //arrange
        runTest {
            val projects = projectRepository.getDocuments(user1.team).first()
            val project = projects.first()

            //act
            val audits = auditRepository.getAuditsByProjectId(project.projectId)?.first()

            //assert
            assertNotNull(audits)
            assertEquals(50, audits?.count())
        }
    }

    @Test
    fun testGetAudit() {
        //arrange
        runTest {
            val projects = projectRepository.getDocuments(user1.team).first()
            val project = projects.first()
            val audits = auditRepository.getAuditsByProjectId(project.projectId)?.first()
            val audit = audits?.first()

            //arrange
            assertNotNull(audit)
            audit?.let {
                val testAudit =
                    auditRepository.get(projectId = project.projectId, auditId = it.auditId)

                //assert
                assertNotNull(testAudit)
                assertEquals(it.auditId, testAudit.auditId)
                assertEquals(it.stockItem, testAudit.stockItem)
                assertEquals(it.notes, testAudit.notes)
                assertEquals(it.auditCount, testAudit.auditCount)
                assertEquals(it.team, testAudit.team)
            }
        }
    }

    @Test
    fun testUpdateAuditStockItem() {
        //arrange
        runTest {
            val projects = projectRepository.getDocuments(user1.team).first()
            val project = projects.first()
            val audits = auditRepository.getAuditsByProjectId(project.projectId)?.first()
            val audit = audits?.first()

            //arrange
            assertNotNull(audit)
            audit?.let {
                val stockItems = stockItemRepository.get()
                val stockItem = stockItems.random()
                auditRepository.updateAuditStockItem(it.projectId, it.auditId, stockItem)
                val testAudit = auditRepository.get(it.projectId, it.auditId)

                //assert
                assertNotNull(testAudit)
                assertEquals(stockItem, testAudit.stockItem)
            }
        }
    }

    @Test
    fun testGetStockItems() {
        //arrange
        runTest {
            //act
            val stockItems = stockItemRepository.get()

            //assert
            assertEquals(3000, stockItems.count())
        }
    }

    @Test
    fun testStockItemCount(){
        //arrange
        runTest {
            //act
            val stockItemCount = stockItemRepository.count()

            //assert
            assertEquals(3000, stockItemCount)
        }
    }

    @Test
    fun testGetStockItemsByNameDescription(){
        //arrange
        runTest {
            val stockItems = stockItemRepository.get()
            val stockItem = stockItems.first()
            //act
            val testStockItemsByName = stockItemRepository.getByNameDescription(stockItem.name.substring(0,5), null)
            val testStockItemsByNameDescription = stockItemRepository.getByNameDescription(stockItem.name.substring(0, 2), stockItem.description.substring(0, 2))

            //assert
            assertTrue(testStockItemsByName.contains(stockItem))
            assertTrue(testStockItemsByNameDescription.contains(stockItem))
        }
    }

    private fun getDemoUser1ProfileDictionary(): MutableMap<String, String> {
        val dict = mutableMapOf<String, String>()
        dict["givenName"] = demoUser1GivenName
        dict["surname"] = demoUser1Surname
        dict["jobTitle"] = demoUser1JobTitle
        dict["team"] = user1.team
        dict["email"] = user1.username
        dict["documentType"] = "user"
        return dict
    }
}