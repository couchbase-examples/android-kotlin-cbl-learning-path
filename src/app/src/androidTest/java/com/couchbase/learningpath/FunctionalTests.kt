package com.couchbase.learningpath

import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import com.couchbase.learningpath.services.MockAuthenticationService
import com.couchbase.learningpath.ui.MainActivity
import com.couchbase.learningpath.ui.components.Drawer
import com.couchbase.learningpath.ui.theme.LearningPathTheme

@RunWith(AndroidJUnit4::class)
class FunctionalTests {
    //LoginView keys
    private val keyTfUsername = "username"
    private val keyTfPassword = "password"
    private val keyBtnLogin = "login"

    //UserProfileView keys
    private val keyLblEmail = "email"
    private val keyTfFirstName = "firstName"
    private val keyTfLastName = "lastName"
    private val keyTfJobTitle = "jobTitle"
    private val keyBtnSave = "save"

    //Overflow Menu keys
    private val keyAppBarMenu = "appBarMenu"
    private val keyBtnMenu = "menu"
    private val keyBtnLogout = "logout"

    //sample data
    private val testUsername1 = "demo@example.com"
    private val testPassword = "password"
    private val testFirstName1 = "Bob"
    private val testLastName1 = "Smith"
    private val testJobTitle1 = "Developer"

    private val testUsername2 = "demo1@example.com"
    private val testFirstName2 = "Jane"
    private val testLastName2 = "Doe"
    private val testJobTitle2 = "Sr. Developer Advocate"

    @OptIn(ExperimentalMaterialApi::class)
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun resetData() {
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Test
    fun testApplicationWorkflow() {

        var resources = launchLoginScreenWithNavGraph()
        composeTestRule.waitForIdle()

        //authenticate as testUser1
        testAuthentication(testUsername1, testPassword, resources)

        //test filling out profile for testUser1 and assert values are in fields properly
        testUserProfileForm(
            firstName = testFirstName1,
            lastName = testLastName1,
            jobTitle = testJobTitle1,
            resources = resources
        )

        assertUserProfile(testFirstName1, testLastName1, testJobTitle1, testUsername1, resources)

        //save testUser1 to database
        composeTestRule.onNodeWithContentDescription(resources[keyBtnSave].toString())
            .performClick()
        composeTestRule.waitForIdle()

        //logout
        logout(resources)
        composeTestRule.waitForIdle()

        //authenticate as testUser2
        testAuthentication(testUsername2, testPassword, resources)

        //fill out form for testUser2
        testUserProfileForm(
            firstName = testFirstName2,
            lastName = testLastName2,
            jobTitle = testJobTitle2,
            resources = resources
        )

        assertUserProfile(testFirstName2, testLastName2, testJobTitle2, testUsername2, resources)
        //save testUser2 to database
        composeTestRule.onNodeWithContentDescription(resources[keyBtnSave].toString())
            .performClick()
        composeTestRule.waitForIdle()

        //logout testUser2
        logout(resources)
        composeTestRule.waitForIdle()

        //authenticate as testUser1
        testAuthentication(testUsername1, testPassword, resources)

        assertUserProfile(testFirstName1, testLastName1, testJobTitle1, testUsername1, resources)
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun testAuthentication(
        username: String,
        password: String,
        resources: Map<String, String>
    ) {
        //set the TextFields values
        composeTestRule.onNodeWithContentDescription(resources[keyTfUsername].toString())
            .performTextInput(username)
        composeTestRule.onNodeWithContentDescription(resources[keyTfPassword].toString())
            .performTextInput(password)

        //assert the TextField has values
        composeTestRule.onNodeWithContentDescription(resources[keyTfUsername].toString())
            .assert(hasText(username, ignoreCase = true))

        composeTestRule.onNodeWithContentDescription(resources[keyBtnLogin].toString())
            .performClick()
        composeTestRule.waitForIdle()
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun testUserProfileForm(
        firstName: String,
        lastName: String,
        jobTitle: String,
        resources: Map<String, String>
    ) {
        composeTestRule.onNodeWithContentDescription(resources[keyTfFirstName].toString())
            .performTextInput(firstName)
        composeTestRule.onNodeWithContentDescription(resources[keyTfLastName].toString())
            .performTextInput(lastName)
        composeTestRule.onNodeWithContentDescription(resources[keyTfJobTitle].toString())
            .performTextInput(jobTitle)
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun assertUserProfile(
        firstName: String,
        lastName: String,
        jobTitle: String,
        email: String,
        resources: Map<String, String>
    ) {
        //assert the email field has the username in it
        composeTestRule.onNodeWithContentDescription(resources[keyLblEmail].toString())
            .assert(hasText(email, ignoreCase = true))

        //assert text fields have proper values
        composeTestRule.onNodeWithContentDescription(resources[keyTfFirstName].toString())
            .assert(hasText(firstName, ignoreCase = true))
        composeTestRule.onNodeWithContentDescription(resources[keyTfLastName].toString())
            .assert(hasText(lastName, ignoreCase = true))
        composeTestRule.onNodeWithContentDescription(resources[keyTfJobTitle].toString())
            .assert(hasText(jobTitle, ignoreCase = true))
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun logout(resources: Map<String, String>) {
        composeTestRule.onNodeWithContentDescription(resources[keyAppBarMenu].toString())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription(resources[keyBtnLogout].toString())
            .performClick()
    }

    @OptIn(ExperimentalMaterialApi::class)
    private fun launchLoginScreenWithNavGraph(): Map<String, String> {
        var resources = HashMap<String, String>()
        composeTestRule.setContent {

            // ** get resources for testing **
            // ** LoginView
            resources[keyTfUsername] = stringResource(id = R.string.tfUsername)
            resources[keyTfPassword] = stringResource(id = R.string.tfPassword)
            resources[keyBtnLogin] = stringResource(id = R.string.btnLogin)

            //** UserProfileView
            resources[keyLblEmail] = stringResource(id = R.string.lblEmail)
            resources[keyTfFirstName] = stringResource(id = R.string.tfGivenName)
            resources[keyTfLastName] = stringResource(id = R.string.tfSurname)
            resources[keyTfJobTitle] = stringResource(id = R.string.tfJobTitle)
            resources[keyBtnSave] = stringResource(id = R.string.btnSave)

            //* Overflow Menu
            resources[keyAppBarMenu] = stringResource(id = R.string.btnAppBarMenu)
            resources[keyBtnMenu] = stringResource(id = R.string.btnMenu)
            resources[keyBtnLogout] = stringResource(id = R.string.btnLogout)

            ProvideWindowInsets {

                val context = LocalContext.current

                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val authService = MockAuthenticationService()

                //we need a drawer overflow menu on multiple screens
                //so we need top level scaffold.  An event to open the drawer is passed
                //to each screen that needs it.
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val openDrawer = {
                    scope.launch {
                        drawerState.open()
                    }
                }

                LearningPathTheme() {
                    Scaffold(scaffoldState = scaffoldState,
                        snackbarHost = {
                            scaffoldState.snackbarHostState
                        }) {
                        ModalDrawer(
                            modifier = Modifier.semantics { contentDescription = "overflowMenu" },
                            drawerState = drawerState,
                            gesturesEnabled = drawerState.isOpen,
                            drawerContent = {
                                Drawer(
                                    modifier = Modifier.semantics {
                                        contentDescription = "overflowMenu1"
                                    },
                                    firstName =  "",
                                    lastName = "",
                                    email = "",
                                    team = "",
                                    profilePicture = null,
                                    onClicked = { route ->
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        when (route) {
                                            MainDestinations.LOGOUT_ROUTE -> {
                                                authService.logout()
                                                navController.navigate(MainDestinations.LOGIN_ROUTE) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        inclusive = true
                                                    }
                                                }
                                            }
                                            else -> {
                                                navController.navigate(route) {
                                                    popUpTo(navController.graph.startDestinationId)
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        ) {
                            InventoryNavGraph(
                                openDrawer = { openDrawer() },
                                navController = navController,
                                scaffoldState = scaffoldState,
                                scope = scope
                            )
                        }
                    }
                }
            }
        }
        return resources
    }
}
