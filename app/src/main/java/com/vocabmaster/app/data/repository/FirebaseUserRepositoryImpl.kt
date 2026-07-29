package com.vocabmaster.app.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.vocabmaster.app.domain.models.User
import com.vocabmaster.app.domain.repository.AuthResult
import com.vocabmaster.app.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class FirebaseUserRepositoryImpl(
    private val auth: FirebaseAuth
) : UserRepository {

    override fun getCurrentUser(): User? = auth.currentUser?.toDomain()

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user?.toDomain() ?: return AuthResult.Error("Giriş başarısız oldu.")
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(mapError(e))
        }
    }

    override suspend fun register(email: String, password: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user?.toDomain() ?: return AuthResult.Error("Kayıt başarısız oldu.")
            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(mapError(e))
        }
    }

    override fun logout() {
        auth.signOut()
    }

    private fun mapError(e: Exception): String = when (e) {
        is FirebaseAuthInvalidUserException -> "Bu e-posta ile kayıtlı bir kullanıcı bulunamadı."
        is FirebaseAuthInvalidCredentialsException -> "E-posta veya şifre hatalı."
        is FirebaseAuthUserCollisionException -> "Bu e-posta adresi zaten kullanımda."
        is FirebaseAuthWeakPasswordException -> "Şifre çok zayıf. En az 6 karakter kullanın."
        is FirebaseNetworkException -> "İnternet bağlantınızı kontrol edin."
        else -> "Bir hata oluştu. Lütfen tekrar deneyin."
    }

    private fun FirebaseUser.toDomain() = User(
        id = uid,
        username = displayName?.takeIf { it.isNotBlank() } ?: (email ?: "").substringBefore("@"),
        email = email ?: "",
        wordsLearnedCount = 0,
        examsCompletedCount = 0
    )
}
