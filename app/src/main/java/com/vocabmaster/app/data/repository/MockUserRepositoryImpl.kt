package com.vocabmaster.app.data.repository

import com.vocabmaster.app.domain.models.User
import com.vocabmaster.app.domain.repository.UserRepository
import kotlinx.coroutines.delay

class MockUserRepositoryImpl : UserRepository {
    private var currentUser: User? = null

    override suspend fun getCurrentUser(): User? {
        delay(300)
        return currentUser
    }

    override suspend fun login(email: String, password: String): Boolean {
        delay(1500)
        if (email == "test@test.com" && password == "123456") {
            currentUser = User("u1", "testuser", email, 42, 5)
            return true
        }
        return false
    }

    override suspend fun logout() {
        delay(300)
        currentUser = null
    }
}
