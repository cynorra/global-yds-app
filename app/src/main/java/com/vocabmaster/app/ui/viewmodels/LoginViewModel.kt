package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.vocabmaster.app.data.repository.FirebaseUserRepositoryImpl
import com.vocabmaster.app.domain.repository.AuthResult
import com.vocabmaster.app.domain.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // In a real app, inject this repository via Dagger/Hilt
    private val userRepository: UserRepository = FirebaseUserRepositoryImpl(FirebaseAuth.getInstance())

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, pass: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.login(email, pass)
            _authResult.postValue(result)
            _isLoading.postValue(false)
        }
    }

    fun loginWithGoogle(idToken: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.loginWithGoogle(idToken)
            _authResult.postValue(result)
            _isLoading.postValue(false)
        }
    }
}
