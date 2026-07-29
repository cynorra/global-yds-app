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

class RegisterViewModel : ViewModel() {
    private val userRepository: UserRepository = FirebaseUserRepositoryImpl(FirebaseAuth.getInstance())

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.register(email, password)
            _authResult.postValue(result)
            _isLoading.postValue(false)
        }
    }
}
