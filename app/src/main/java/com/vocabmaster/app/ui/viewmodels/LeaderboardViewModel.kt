package com.vocabmaster.app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vocabmaster.app.domain.models.LeaderboardUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LeaderboardViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _users = MutableLiveData<List<LeaderboardUser>>()
    val users: LiveData<List<LeaderboardUser>> get() = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchLeaderboard() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("users")
                    .orderBy("totalScore", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .await()
                
                val userList = snapshot.documents.mapNotNull { it.toObject(LeaderboardUser::class.java) }
                _users.value = userList
            } catch (e: Exception) {
                _users.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
