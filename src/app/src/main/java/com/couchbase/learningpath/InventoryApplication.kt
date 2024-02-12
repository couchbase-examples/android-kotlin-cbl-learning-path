package com.couchbase.learningpath

import android.app.Application
import com.couchbase.learningpath.data.DatabaseManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.dsl.module

import com.couchbase.learningpath.data.KeyValueRepository
import com.couchbase.learningpath.data.audits.AuditRepository
import com.couchbase.learningpath.data.audits.AuditRepositoryDb
import com.couchbase.learningpath.data.project.ProjectRepository
import com.couchbase.learningpath.data.project.ProjectRepositoryDb
import com.couchbase.learningpath.data.stockItem.StockItemRepository
import com.couchbase.learningpath.data.stockItem.StockItemRepositoryDb
import com.couchbase.learningpath.data.userprofile.UserProfileRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepository
import com.couchbase.learningpath.data.warehouse.WarehouseRepositoryDb
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.services.MockAuthenticationService
import com.couchbase.learningpath.services.ReplicatorService
import com.couchbase.learningpath.services.ReplicatorServiceDb
import com.couchbase.learningpath.ui.MainViewModel
import com.couchbase.learningpath.ui.audit.AuditEditorViewModel
import com.couchbase.learningpath.ui.audit.AuditListViewModel
import com.couchbase.learningpath.ui.audit.StockItemSelectionViewModel
import com.couchbase.learningpath.ui.developer.DevDatabaseInfoViewModel
import com.couchbase.learningpath.ui.developer.DeveloperViewModel
import com.couchbase.learningpath.ui.developer.ReplicatorConfigViewModel
import com.couchbase.learningpath.ui.developer.ReplicatorViewModel
import com.couchbase.learningpath.ui.login.LoginViewModel
import com.couchbase.learningpath.ui.profile.UserProfileViewModel
import com.couchbase.learningpath.ui.project.WarehouseSelectionViewModel
import com.couchbase.learningpath.ui.project.ProjectEditorViewModel
import com.couchbase.learningpath.ui.project.ProjectListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

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
    @OptIn(InternalCoroutinesApi::class)
    private fun registerDependencies() : Module {
        return module {
            singleOf(::DatabaseManager)
            singleOf(::MockAuthenticationService) bind AuthenticationService::class
            singleOf(::ReplicatorServiceDb) bind ReplicatorService::class
            singleOf(::UserProfileRepository) bind KeyValueRepository::class
            singleOf(::WarehouseRepositoryDb) bind WarehouseRepository::class
            singleOf(::ProjectRepositoryDb) bind ProjectRepository::class
            singleOf(::StockItemRepositoryDb) bind StockItemRepository::class
            singleOf(::AuditRepositoryDb) bind AuditRepository::class

            viewModelOf(::MainViewModel)
            viewModelOf(::LoginViewModel)
            viewModelOf(::ProjectListViewModel)
            viewModelOf(::ProjectEditorViewModel)
            viewModelOf(::AuditListViewModel)
            viewModelOf(::AuditEditorViewModel)

            viewModelOf(::WarehouseSelectionViewModel)
            viewModelOf(::StockItemSelectionViewModel)
            viewModelOf(::UserProfileViewModel)
            viewModelOf(::DeveloperViewModel)
            viewModelOf(::DevDatabaseInfoViewModel)
            viewModelOf(::ReplicatorViewModel)
            viewModelOf(::ReplicatorConfigViewModel)
        }
    }
}