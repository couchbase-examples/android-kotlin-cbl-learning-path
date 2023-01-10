package com.couchbase.learningpath.services

import androidx.lifecycle.LiveData
import com.couchbase.learningpath.models.User

interface AuthenticationService {
    val currentUser: LiveData<User?>
    fun getCurrentUser() : User
    suspend fun authenticatedUser(username: String, password: String) : Boolean
    fun logout()
}