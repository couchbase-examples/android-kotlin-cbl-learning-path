package com.couchbase.learningpath.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.services.ReplicatorService

class MainViewModel(
    private val authService: AuthenticationService,
    private val replicatorService: ReplicatorService,
    private val databaseManager: DatabaseManager
) : ViewModel() {

    val startDatabase: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            databaseManager.initializeDatabases(authService.getCurrentUser())
        }
    }

    val closeDatabase: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            replicatorService.stopReplication()
            databaseManager.closeDatabases()
        }
    }
}