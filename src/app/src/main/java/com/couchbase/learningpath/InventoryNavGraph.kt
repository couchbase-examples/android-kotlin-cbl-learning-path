package com.couchbase.learningpath

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.couchbase.learningpath.ui.audit.*
import com.couchbase.learningpath.ui.developer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import java.util.*

import com.couchbase.learningpath.ui.login.LoginView
import com.couchbase.learningpath.ui.login.LoginViewModel
import com.couchbase.learningpath.ui.profile.UserProfileView
import com.couchbase.learningpath.ui.profile.UserProfileViewModel
import com.couchbase.learningpath.ui.project.*

/*
    Destinations used in routing
 */
object MainDestinations {
    const val LOGIN_ROUTE = "login"
    const val USERPROFILE_ROUTE = "userprofile"
    const val PROJECT_LIST_ROUTE = "project_list"
    const val DEVELOPER_ROUTE = "developer"
    const val DEVELOPER_DATABASE_INFO_ROUTE = "developer_database_info"
    const val REPLICATOR_ROUTE = "replicator"
    const val REPLICATOR_SETTINGS_ROUTE = "replicatorConfig"
    const val LOGOUT_ROUTE = "logout"

    const val PROJECT_EDITOR_ROUTE = "project_editor"
    const val PROJECT_EDITOR_ROUTE_PATH = "project_editor/{projectId}"
    const val PROJECT_KEY_ID = "projectId"

    const val LOCATION_LIST_ROUTE = "location_list"
    const val LOCATION_ROUTE_PATH = "location_list/{projectId}"
    const val LOCATION_LIST_KEY_ID = "projectId"

    const val AUDIT_LIST_ROUTE_PATH = "auditList/{project}"
    const val AUDIT_LIST_ROUTE = "auditList"
    const val AUDIT_LIST_KEY_ID = "project"

    const val AUDIT_EDITOR_ROUTE_PATH = "auditEditor/{projectId}/{audit}"
    const val AUDIT_EDITOR_ROUTE = "auditEditor"
    const val AUDIT_EDITOR_KEY_ID = "audit"

    const val STOCK_ITEM_ROUTE_PATH = "stock_item_list/{projectId}/{audit}"
    const val STOCK_ITEM_LIST_ROUTE = "stock_item_list"
}

//main function for handling navigation graph in the app
@kotlinx.serialization.ExperimentalSerializationApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
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
                navigateToProjectEditor = actions.navigateToProjectEditor,
                navigateToAuditListByProject = actions.navigateToAuditListByProject,
                viewModel = getViewModel<ProjectListViewModel>())
        }

        composable(MainDestinations.AUDIT_LIST_ROUTE_PATH){ backstackEntry ->
            val projectJson = backstackEntry.arguments?.getString(MainDestinations.AUDIT_LIST_KEY_ID)
            projectJson?.let {
                val viewModel = getViewModel<AuditListViewModel>()
                viewModel.projectJson = it
                viewModel.getAudits()
                AuditListView(
                    viewModel = viewModel,
                    actions.upPress,
                    actions.navigateToAuditEditor,
                    scaffoldState = scaffoldState,
                    snackBarCoroutineScope = scope
                )
            }
        }

        composable(MainDestinations.PROJECT_EDITOR_ROUTE_PATH ) { backstackEntry ->
            val projectId = backstackEntry.arguments?.getString(MainDestinations.PROJECT_KEY_ID)
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

        composable(MainDestinations.AUDIT_EDITOR_ROUTE_PATH){ backstackEntry ->
            var projectId = ""
            var auditId = "create"

            val argProjectId = backstackEntry.arguments?.getString(MainDestinations.PROJECT_KEY_ID)
            val argAuditId = backstackEntry.arguments?.getString(MainDestinations.AUDIT_EDITOR_KEY_ID )

            argProjectId?.let {
                projectId = it
            }
            argAuditId?.let {
                auditId = it
            }
            val viewModel = getViewModel<AuditEditorViewModel>()
                viewModel.getAudit(projectId = projectId, auditId = auditId)
                viewModel.navigateToListSelection = actions.navigateToStockItemListSelector

            AuditEditorView(
                viewModel = viewModel,
                navigateUp = actions.upPress
            )
        }

        composable(MainDestinations.LOCATION_ROUTE_PATH) {  backstackEntry ->
            val projectId = backstackEntry.arguments?.getString(MainDestinations.LOCATION_LIST_KEY_ID)
            projectId?.let {
                val viewModel = getViewModel<WarehouseSelectionViewModel>()
                viewModel.projectId(it)
                WarehouseSelectionView(
                    viewModel = viewModel,
                    navigateUp = actions.upPress
                )
            }
        }

        composable(MainDestinations.STOCK_ITEM_ROUTE_PATH) {  backstackEntry ->
            var projectId = ""
            var auditId = ""

            val argProjectId = backstackEntry.arguments?.getString(MainDestinations.PROJECT_KEY_ID)
            val argAuditId = backstackEntry.arguments?.getString(MainDestinations.AUDIT_EDITOR_KEY_ID )

            argProjectId?.let {
                projectId = it
            }
            argAuditId?.let {
                auditId = it
            }

            val viewModel = getViewModel<StockItemSelectionViewModel>()
            val auditEditorViewModel = getViewModel<AuditEditorViewModel>()

            viewModel.projectId(projectId)
            viewModel.auditId(auditId)

            viewModel.navigateUp = {
                auditEditorViewModel.loadAudit()
                actions.upPress()
            }

            StockItemSelectionView(
                    viewModel = viewModel,
                    navigateUp = {
                        auditEditorViewModel.loadAudit()
                        actions.upPress() }
            )
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

        composable(MainDestinations.REPLICATOR_ROUTE){
            ReplicatorView(
                viewModel = getViewModel<ReplicatorViewModel>(),
                openDrawer = openDrawer,
                replicatorConfigNav = actions.navigateToReplicatorConfig,
                scaffoldState = scaffoldState
            )
        }

        composable(MainDestinations.REPLICATOR_SETTINGS_ROUTE){
            ReplicatorConfigView(
                viewModel = getViewModel<ReplicatorConfigViewModel>(),
                navigateUp = actions.upPress,
                scaffoldState = scaffoldState
            )
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

    val navigateToStockItemListSelector: (String, String) -> Unit = { projectId: String, auditId: String ->
        navController.navigate("${MainDestinations.STOCK_ITEM_LIST_ROUTE}/$projectId/$auditId")
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

    val navigateToAuditListByProject: (String) -> Unit = { projectJson: String ->
        navController.navigate("${MainDestinations.AUDIT_LIST_ROUTE}/$projectJson")
    }

    val navigateToAuditEditor:(String, String) -> Unit = { projectId: String, audit: String ->
        navController.navigate("${MainDestinations.AUDIT_EDITOR_ROUTE}/$projectId/$audit")
    }

    val navigateToReplicatorConfig: () -> Unit = {
        navController.navigate(MainDestinations.REPLICATOR_SETTINGS_ROUTE)
    }

    val upPress: () -> Unit = {
        navController.popBackStack()
    }
}