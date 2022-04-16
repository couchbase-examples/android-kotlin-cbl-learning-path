package com.couchbase.learningpath.services

import com.couchbase.learningpath.models.User

interface AuthenticationService {
    fun getCurrentUser() : User
    fun authenticatedUser(username: String, password: String) : Boolean
    fun logout()
}