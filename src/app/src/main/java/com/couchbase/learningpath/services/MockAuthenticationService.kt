package com.couchbase.learningpath.services

import com.couchbase.learningpath.models.User

class MockAuthenticationService : AuthenticationService {

    private var _user: User? = null
    private var _mockUsers = HashMap<String, User>()

    override fun getCurrentUser(): User? {
        return _user
    }

    override fun authenticatedUser(username: String, password: String): Boolean {
        return if (_mockUsers.containsKey(username)){
            val user = _mockUsers[username]
            if (user?.password == password){
                _user = user
                true
            } else {
                false
            }
        } else {
            _user = User(username = username, password = password, team = "Santa Clara Team")
            return true
        }
    }

    override fun logout() {
        _user = null
    }

    init {
        //create mock users for testing the application
        //in a real app this would be provided by some kind of OAuth2 Service, etc
        _mockUsers["demo@example.com"] = User("demo@example.com", "password", "Santa Clara Team")
        _mockUsers["demo1@example.com"] = User("demo1@example.com", "password", "Santa Clara Team")
        _mockUsers["demo2@example.com"] = User("demo2@example.com", "password", "London Team")
        _mockUsers["demo3@example.com"] = User("demo3@example.com", "password", "London Team")
        _mockUsers["demo4@example.com"] = User("demo4@example.com", "password", "Austin Team")
        _mockUsers["demo5@example.com"] = User("demo5@example.com", "password", "Austin Team")
        _mockUsers["demo6@example.com"] = User("demo6@example.com", "password", "San Francisco Team")
        _mockUsers["demo7@example.com"] = User("demo7@example.com", "password", "San Francisco Team")
        _mockUsers["demo8@example.com"] = User("demo8@example.com", "password", "Manchester Team")
        _mockUsers["demo9@example.com"] = User("demo9@example.com", "password", "Manchester Team")
        _mockUsers["demo10@example.com"] = User("demo10@example.com", "password", "Bangalore Team")
        _mockUsers["demo11@example.com"] = User("demo11@example.com", "password", "Bangalore Team")
        _mockUsers["demo12@example.com"] = User("demo12@example.com", "password", "Atlanta Team")
        _mockUsers["demo13@example.com"] = User("demo13@example.com", "password", "Seattle Team")
        _mockUsers["demo14@example.com"] = User("demo14@example.com", "password", "Kinshasa Team")
        _mockUsers["demo15@example.com"] = User("demo15@example.com", "password", "Tokyo Team")
    }
}