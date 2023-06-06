package com.couchbase.learningpath.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController

import com.google.accompanist.insets.ProvideWindowInsets
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel

import com.couchbase.learningpath.InventoryNavGraph
import com.couchbase.learningpath.MainDestinations
import com.couchbase.learningpath.R
import com.couchbase.learningpath.data.KeyValueRepository
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.services.ReplicatorService
import com.couchbase.learningpath.ui.components.Drawer
import com.couchbase.learningpath.ui.profile.UserProfileViewModel
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi

@kotlinx.serialization.ExperimentalSerializationApi
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProvideWindowInsets {
                val scope = rememberCoroutineScope()
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()
                val authService: AuthenticationService by inject()
                val userProfileRepository: KeyValueRepository by inject()
                val replicatorService : ReplicatorService by inject()
                val menuResource = stringResource(id = R.string.btnMenu)
                val mainViewModel = getViewModel<MainViewModel>()

                //used for drawing profile in drawer
                var profileViewModel: UserProfileViewModel? = null

                fun logout() {
                    replicatorService.stopReplication()
                    replicatorService.updateAuthentication(isReset = true)
                    profileViewModel = null
                    authService.logout()
                }
                //we need a drawer overflow menu on multiple screens
                //so we need top level scaffold.  An event to open the drawer is passed
                //to each screen that needs it.
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val openDrawer = {
                    scope.launch {
                        if (profileViewModel == null) {
                            profileViewModel = UserProfileViewModel(
                                application = application,
                                repository = userProfileRepository,
                                authService = authService,
                            )
                        } else {
                            profileViewModel?.updateUserProfileInfo()
                        }
                        drawerState.open()
                    }
                }

                LearningPathTheme {
                    Scaffold(scaffoldState = scaffoldState,
                        snackbarHost = {
                            scaffoldState.snackbarHostState
                        }) {
                        ModalDrawer(
                            modifier = Modifier.semantics { contentDescription = menuResource },
                            drawerState = drawerState,
                            gesturesEnabled = drawerState.isOpen,
                            drawerContent = {
                                Drawer(
                                    modifier = Modifier.semantics { contentDescription = "{$menuResource}1" },
                                    firstName = profileViewModel?.givenName?.value,
                                    lastName =  profileViewModel?.surname?.value,
                                    email = profileViewModel?.emailAddress?.value,
                                    team = profileViewModel?.team?.value,
                                    profilePicture = profileViewModel?.profilePic?.value,
                                    onClicked = { route ->
                                        scope.launch {
                                            drawerState.close()
                                        }
                                        when (route) {
                                            MainDestinations.LOGOUT_ROUTE -> {
                                                logout()
                                                navController.navigate(MainDestinations.LOGIN_ROUTE){
                                                    popUpTo(navController.graph.findStartDestination().id){
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
                            MainView(
                                mainViewModel.startDatabase,
                                mainViewModel.closeDatabase)
                            InventoryNavGraph(
                                openDrawer =  { openDrawer() },
                                navController = navController,
                                scaffoldState = scaffoldState,
                                scope = scope
                            )
                        }
                    }
                }
            }
        }
    }
}

// **
// handle lifecycle events when app goes to background and comes back
// this handles closing and opening the database properly
// https://developer.android.com/jetpack/compose/side-effects#disposableeffect
// **
@Composable
fun MainView(startDatabase: () -> Unit,
             closeDatabase: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Safely update the current lambdas when a new one is provided
    val currentOnStart by rememberUpdatedState(startDatabase)
    val currentOnStop by rememberUpdatedState(closeDatabase)

    //if lifecycleOwner changes, dispose and reset the effect
    DisposableEffect(lifecycleOwner) {
        // Create an observer that triggers our remembered callbacks
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Log.w("event","DEBUG:  DANGER, WILL ROBINSON!!  opening the database due to event: ${event.name}")
                    currentOnStart()
                }
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    Log.w("event","DEBUG:  DANGER, WILL ROBINSON!!  closing the database due to event: ${event.name}")
                    currentOnStop()
                }
                else -> {
                    Log.w("event","DEBUG:  DANGER, WILL ROBINSON!!  Event happened that we don't handle ${event.name}")
                }
            }
        }
        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
