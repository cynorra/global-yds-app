package com.vocabmaster.app.domain.repository

import com.vocabmaster.app.domain.models.User

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

interface UserRepository {
    fun getCurrentUser(): User?
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String): AuthResult
    fun logout()
}
