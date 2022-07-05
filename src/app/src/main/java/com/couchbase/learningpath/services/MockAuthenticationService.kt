package com.couchbase.learningpath.services

import com.couchbase.learningpath.models.User

class MockAuthenticationService : AuthenticationService {

    private var _user: User? = null
    private var _mockUsers = HashMap<String, User>()

    override fun getCurrentUser(): User {
        return _user?: User("", "", "")
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
            _user = User(username = username, password = password, team = "team2")
            return true
        }
    }

    override fun logout() {
        _user = null
    }

    init {
        //create mock users for testing the application
        //in a real app this would be provided by some kind of OAuth2 Service, etc
        _mockUsers["demo@example.com"] = User("demo@example.com", "P@ssw0rd12", "team1")
        _mockUsers["demo1@example.com"] = User("demo1@example.com", "P@ssw0rd12", "team1")
        _mockUsers["demo2@example.com"] = User("demo2@example.com", "P@ssw0rd12", "team2")
        _mockUsers["demo3@example.com"] = User("demo3@example.com", "P@ssw0rd12", "team2")
        _mockUsers["demo4@example.com"] = User("demo4@example.com", "P@ssw0rd12", "team3")
        _mockUsers["demo5@example.com"] = User("demo5@example.com", "P@ssw0rd12", "team3")
        _mockUsers["demo6@example.com"] = User("demo6@example.com", "P@ssw0rd12", "team4")
        _mockUsers["demo7@example.com"] = User("demo7@example.com", "P@ssw0rd12", "team4")
        _mockUsers["demo8@example.com"] = User("demo8@example.com", "P@ssw0rd12", "team5")
        _mockUsers["demo9@example.com"] = User("demo9@example.com", "P@ssw0rd12", "team5")
        _mockUsers["demo10@example.com"] = User("demo10@example.com", "P@ssw0rd12", "team6")
        _mockUsers["demo11@example.com"] = User("demo11@example.com", "P@ssw0rd12", "team6")
        _mockUsers["demo12@example.com"] = User("demo12@example.com", "P@ssw0rd12", "team7")
        _mockUsers["demo13@example.com"] = User("demo13@example.com", "P@ssw0rd12", "team8")
        _mockUsers["demo14@example.com"] = User("demo14@example.com", "P@ssw0rd12", "team9")
        _mockUsers["demo15@example.com"] = User("demo15@example.com", "P@ssw0rd12", "team10")
    }
}