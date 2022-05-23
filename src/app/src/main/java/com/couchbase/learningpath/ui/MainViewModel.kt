package com.couchbase.learningpath.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.services.ReplicatorService

class MainViewModel(
    private val authService: AuthenticationService,
    private val replicatorService: ReplicatorService,
    val context: WeakReference<Context>,

) : ViewModel() {

    val startDatabase: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            context.get()?.let {
                DatabaseManager.getInstance(it).initializeDatabases(authService.getCurrentUser())
            }
        }
    }

    val closeDatabase: () -> Unit = {
        viewModelScope.launch(Dispatchers.IO) {
            context.get()?.let {
                replicatorService.stopReplication()
                DatabaseManager.getInstance(it).closeDatabases()
            }
        }
    }
}