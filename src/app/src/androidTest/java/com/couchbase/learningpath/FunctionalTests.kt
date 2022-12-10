package com.couchbase.learningpath

import androidx.compose.material.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import com.couchbase.learningpath.ui.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalMaterialApi::class, ExperimentalSerializationApi::class, ExperimentalCoroutinesApi::class)
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
    private val testPassword = "P@ssw0rd12"
    private val testFirstName1 = "Bob"
    private val testLastName1 = "Smith"
    private val testJobTitle1 = "Developer"

    private val testUsername2 = "demo1@example.com"
    private val testFirstName2 = "Jane"
    private val testLastName2 = "Doe"
    private val testJobTitle2 = "Sr. Developer Advocate"

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun resetData() {
    }

    @Test
    fun testApplicationWorkflow() {

        val resources = getResources()
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

        composeTestRule.onNodeWithContentDescription(resources[keyAppBarMenu].toString())
            .performClick()

        composeTestRule.onNodeWithText("Update User Profile")
            .performClick()

        assertUserProfile(testFirstName1, testLastName1, testJobTitle1, testUsername1, resources)
    }

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

    private fun testUserProfileForm(
        firstName: String,
        lastName: String,
        jobTitle: String,
        resources: Map<String, String>
    ) {
        composeTestRule.onNodeWithContentDescription(resources[keyAppBarMenu].toString())
            .performClick()

        composeTestRule.onNodeWithText("Update User Profile")
            .performClick()

        composeTestRule.onNodeWithContentDescription(resources[keyTfFirstName].toString()).apply {
            performTextClearance()
            performTextInput(firstName)
        }
        composeTestRule.onNodeWithContentDescription(resources[keyTfLastName].toString()).apply {
            performTextClearance()
            performTextInput(lastName)
        }
        composeTestRule.onNodeWithContentDescription(resources[keyTfJobTitle].toString()).apply {
            performTextClearance()
            performTextInput(jobTitle)
        }
    }

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

    private fun logout(resources: Map<String, String>) {
        composeTestRule.onNodeWithContentDescription(resources[keyAppBarMenu].toString())
            .performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription(resources[keyBtnLogout].toString())
            .performClick()
    }

    private fun getResources(): Map<String, String> {
        val resources = HashMap<String, String>()
        with(composeTestRule.activity.resources) {
            // ** get resources for testing **
            // ** LoginView
            resources[keyTfUsername] = getString(R.string.tfUsername)
            resources[keyTfPassword] = getString(R.string.tfPassword)
            resources[keyBtnLogin] = getString(R.string.btnLogin)

            //** UserProfileView
            resources[keyLblEmail] = getString(R.string.lblEmail)
            resources[keyTfFirstName] = getString(R.string.tfGivenName)
            resources[keyTfLastName] = getString(R.string.tfSurname)
            resources[keyTfJobTitle] = getString(R.string.tfJobTitle)
            resources[keyBtnSave] = getString(R.string.btnSave)

            //* Overflow Menu
            resources[keyAppBarMenu] = getString(R.string.btnAppBarMenu)
            resources[keyBtnMenu] = getString(R.string.btnMenu)
            resources[keyBtnLogout] = getString(R.string.btnLogout)
        }
        return resources
    }
}
