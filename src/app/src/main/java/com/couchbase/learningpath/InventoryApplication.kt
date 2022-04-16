package com.couchbase.learningpath

import android.app.Application
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module
import java.lang.ref.WeakReference

import com.couchbase.learningpath.data.KeyValueRepository
import com.couchbase.learningpath.data.location.LocationRepository
import com.couchbase.learningpath.data.location.LocationRepositoryDb
import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.data.project.ProjectRepositoryDb
import com.couchbase.learningpath.data.userprofile.UserProfileRepository
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.services.MockAuthenticationService
import com.couchbase.learningpath.ui.MainViewModel
import com.couchbase.learningpath.ui.developer.DevDatabaseInfoViewModel
import com.couchbase.learningpath.ui.developer.DeveloperViewModel
import com.couchbase.learningpath.ui.login.LoginViewModel
import com.couchbase.learningpath.ui.profile.UserProfileViewModel
import com.couchbase.learningpath.ui.project.LocationSelectionViewModel
import com.couchbase.learningpath.ui.project.ProjectEditorViewModel
import com.couchbase.learningpath.ui.project.ProjectListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalCoroutinesApi
class InventoryApplication
    : Application() {

    override fun onCreate() {
        super.onCreate()

        // enable Koin dependency injection framework
        // https://insert-koin.io/docs/reference/koin-android/start
        GlobalContext.startKoin {
            // Koin Android logger
            //work around for error: https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)

            //inject Android context
            androidContext(this@InventoryApplication)

            //dependency register modules
            modules(registerDependencies())
        }
    }

    //dependency registration for dependency inversion and injection
    private fun registerDependencies() : Module {
        return module {
            // ** DO NOT listen to the NO cast needed warnings - removing the as statement will
            // ** result in the application not functioning correctly
            single { MockAuthenticationService() as AuthenticationService }
            single { UserProfileRepository(this@InventoryApplication) as KeyValueRepository }
            single { LocationRepositoryDb(this@InventoryApplication) as LocationRepository }
            single { ProjectRepositoryDb(this@InventoryApplication, get(), get()) as ProjectRepository }
            viewModel { MainViewModel(get(), WeakReference(this@InventoryApplication)) }
            viewModel { LoginViewModel(get(), WeakReference(this@InventoryApplication)) }
            viewModel { ProjectListViewModel(get(), get()) }
            viewModel { ProjectEditorViewModel(get()) }
            viewModel { LocationSelectionViewModel(get(), get()) }
            viewModel { UserProfileViewModel(get(), get(), WeakReference(this@InventoryApplication)) }
            viewModel { DeveloperViewModel(get()) }
            viewModel { DevDatabaseInfoViewModel(get(), get(), get(), get()) }
        }
    }
}