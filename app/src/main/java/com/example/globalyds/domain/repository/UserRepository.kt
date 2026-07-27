package com.example.globalyds.domain.repository

import com.example.globalyds.domain.models.User

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun login(email: String, password: String): Boolean
    suspend fun logout()
}
