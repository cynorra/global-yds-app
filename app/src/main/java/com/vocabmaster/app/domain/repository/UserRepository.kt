package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.models.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun login(email: String, password: String): Boolean
    suspend fun logout()
}
