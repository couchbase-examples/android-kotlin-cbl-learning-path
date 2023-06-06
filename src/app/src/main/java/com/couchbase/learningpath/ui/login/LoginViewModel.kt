package com.couchbase.learningpath.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.couchbase.learningpath.data.DatabaseManager
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.services.ReplicatorService

class LoginViewModel(
    private val authenticationService: AuthenticationService,
    private val replicatorService: ReplicatorService,
    private val databaseManager: DatabaseManager
) : ViewModel() {

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username
    val onUsernameChanged: (String) -> Unit = { newValue ->
        _isError.value = false
        _username.value = newValue
    }

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password
    val onPasswordChanged: (String) -> Unit = { newValue ->
        _isError.value = false
        _password.value = newValue
    }

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    fun login(): Boolean {
        _username.value?.let { uname ->
            _password.value?.let { pwd ->
                if (authenticationService.authenticatedUser(username = uname, password = pwd)) {
                    _isError.value = false
                    val currentUser = authenticationService.getCurrentUser()
                    viewModelScope.launch(Dispatchers.IO) {
                        //initialize database if needed
                        databaseManager.initializeDatabases(currentUser)
                        replicatorService.updateAuthentication(isReset = false)
                    }
                    return true
                }
            }
        }
        _isError.value = true
        return false
    }
}