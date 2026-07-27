package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabmaster.app.data.repository.MockUserRepositoryImpl
import com.vocabmaster.app.domain.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // In a real app, inject this repository via Dagger/Hilt
    private val userRepository: UserRepository = MockUserRepositoryImpl()

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> = _loginStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, pass: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val success = userRepository.login(email, pass)
            _loginStatus.postValue(success)
            _isLoading.postValue(false)
        }
    }
}
