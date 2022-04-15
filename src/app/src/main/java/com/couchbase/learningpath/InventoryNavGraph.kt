package com.couchbase.learningpath

import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.couchbase.learningpath.ui.developer.DevDatabaseInfoView
import com.couchbase.learningpath.ui.developer.DevDatabaseInfoViewModel
import com.couchbase.learningpath.ui.developer.DeveloperView
import com.couchbase.learningpath.ui.developer.DeveloperViewModel
import com.couchbase.learningpath.ui.login.LoginView
import com.couchbase.learningpath.ui.login.LoginViewModel
import com.couchbase.learningpath.ui.profile.UserProfileView
import com.couchbase.learningpath.ui.profile.UserProfileViewModel
import com.couchbase.learningpath.ui.project.*
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.compose.getViewModel
import java.util.*

/*
    Destinations used in routing
 */
object MainDestinations {
    const val LOGIN_ROUTE = "login"
    const val USERPROFILE_ROUTE = "userprofile"
    const val PROJECT_LIST_ROUTE = "project_list"
    const val PROJECT_EDITOR_ROUTE = "project_editor"
    const val LOCATION_LIST_ROUTE = "location_list"
    const val DEVELOPER_ROUTE = "developer"
    const val DEVELOPER_DATABASE_INFO_ROUTE = "developer_database_info"
    const val LOGOUT_ROUTE = "logout"

    const val PROJECT_EDITOR_ROUTE_PATH = "project_editor/{projectId}"
    const val LOCATION_ROUTE_PATH = "location_list/{projectId}"

    const val PROJECT_EDITOR_KEY_ID = "projectId"
    const val LOCATION_LIST_KEY_ID = "projectId"
}

//main function for handling navigation graph in the app
@Composable
fun InventoryNavGraph(
    openDrawer: () -> Unit,
    navController: NavHostController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = MainDestinations.LOGIN_ROUTE) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(navController = navController,
        startDestination = startDestination) {

        composable(MainDestinations.LOGIN_ROUTE) {
            LoginView(onSuccessLogin = {
                actions.navigateToProjectsListView()
            },
                viewModel = getViewModel<LoginViewModel>())
        }

        composable(MainDestinations.PROJECT_LIST_ROUTE) {
            ProjectListView(
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
                scope = scope,
                viewModel = getViewModel<ProjectListViewModel>(),
                navigateToProjectEditor = actions.navigateToProjectEditor)
        }

        composable(MainDestinations.PROJECT_EDITOR_ROUTE_PATH ) { backstackEntry ->
            val projectId = backstackEntry.arguments?.getString(MainDestinations.PROJECT_EDITOR_KEY_ID)
            val viewModel = getViewModel<ProjectEditorViewModel>()
            if (projectId == null){
                viewModel.projectId(UUID.randomUUID().toString())
            }
            else {
                viewModel.projectId(projectId)
            }
            ProjectEditorView(
                viewModel = viewModel,
                navigateToListSelection = actions.navigateToLocationListSelector,
                navigateUp = actions.upPress,
                scaffoldState = scaffoldState)
        }

        composable(MainDestinations.LOCATION_ROUTE_PATH) {  backstackEntry ->
            val projectId = backstackEntry.arguments?.getString(MainDestinations.LOCATION_LIST_KEY_ID)
            projectId?.let {
                val viewModel = getViewModel<LocationSelectionViewModel>()
                viewModel.projectId(it)
                LocationSelectionView(
                    viewModel = viewModel,
                    navigateUp = actions.upPress
                )
            }
        }

        composable(MainDestinations.USERPROFILE_ROUTE) {
            UserProfileView(
                openDrawer = openDrawer,
                scaffoldState = scaffoldState,
                viewModel = getViewModel<UserProfileViewModel>())
        }

        composable(MainDestinations.DEVELOPER_ROUTE){
            DeveloperView(
                scaffoldState = scaffoldState,
                viewModel = getViewModel<DeveloperViewModel>(),
                openDrawer = openDrawer,
                navigateToDatabaseInfoView = actions.navigateToDeveloperDatabaseInfo)
        }

        composable(MainDestinations.DEVELOPER_DATABASE_INFO_ROUTE){
            DevDatabaseInfoView(
                scaffoldState = scaffoldState,
                navigateUp = actions.upPress,
                viewModel = getViewModel<DevDatabaseInfoViewModel>())
        }

        composable(MainDestinations.LOGOUT_ROUTE){
        }
    }
}

/**
 * Models the navigation actions in the app.
 */
class MainActions(navController: NavHostController) {
    val navigateToLocationListSelector: (String) -> Unit = {projectId: String ->
        navController.navigate("${MainDestinations.LOCATION_LIST_ROUTE}/$projectId")
    }
    val navigateToProjectEditor: (String) -> Unit = { projectId: String ->
        navController.navigate("${MainDestinations.PROJECT_EDITOR_ROUTE}/$projectId")
    }
    val navigateToProjectsListView: () -> Unit = {
        navController.navigate(MainDestinations.PROJECT_LIST_ROUTE)
    }
    val navigateToDeveloperDatabaseInfo:() -> Unit = {
        navController.navigate(MainDestinations.DEVELOPER_DATABASE_INFO_ROUTE)
    }
    val upPress: () -> Unit = {
        navController.popBackStack()
    }
}