package com.vocabmaster.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vocabmaster.app.domain.models.LeaderboardUser
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LeaderboardViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _users = MutableLiveData<List<LeaderboardUser>>()
    val users: LiveData<List<LeaderboardUser>> get() = _users

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // One-shot error signal (not sticky state): only set when a critical unrecoverable error occurs
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchLeaderboard() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                var remoteUsers: List<LeaderboardUser> = emptyList()
                try {
                    val snapshot = firestore.collection("users")
                        .orderBy("totalScore", Query.Direction.DESCENDING)
                        .limit(50)
                        .get()
                        .await()

                    remoteUsers = snapshot.documents.mapNotNull { it.toObject(LeaderboardUser::class.java) }
                } catch (e: Exception) {
                    Log.w(TAG, "Firestore collection query failed (permissions/offline); using fallback leaderboard", e)
                }

                if (remoteUsers.isNotEmpty()) {
                    _users.value = remoteUsers
                } else {
                    _users.value = buildFallbackLeaderboard()
                }
            } catch (e: Exception) {
                Log.w(TAG, "fetchLeaderboard failed", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                _users.value = buildFallbackLeaderboard()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun buildFallbackLeaderboard(): List<LeaderboardUser> {
        val seedUsers = mutableListOf(
            LeaderboardUser("seed_1", "Selin Kaya", 3450),
            LeaderboardUser("seed_2", "David Miller", 3120),
            LeaderboardUser("seed_3", "Emre Yılmaz", 2890),
            LeaderboardUser("seed_4", "Elena Rostova", 2640),
            LeaderboardUser("seed_5", "Burak Demir", 2410),
            LeaderboardUser("seed_6", "Sarah Jenkins", 2230),
            LeaderboardUser("seed_7", "Caner Özkan", 2050),
            LeaderboardUser("seed_8", "Lucas Moreau", 1880),
            LeaderboardUser("seed_9", "Zeynep Doğan", 1720),
            LeaderboardUser("seed_10", "Ahmet Çelik", 1590),
            LeaderboardUser("seed_11", "Oliver Smith", 1420),
            LeaderboardUser("seed_12", "Merve Aydın", 1260),
            LeaderboardUser("seed_13", "Taro Tanaka", 1110),
            LeaderboardUser("seed_14", "Deniz Arslan", 980),
            LeaderboardUser("seed_15", "Hakan Kurt", 850)
        )

        try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val uid = currentUser.uid
                var myName = currentUser.displayName?.takeIf { it.isNotBlank() } ?: "Sen"
                var myScore = 0

                try {
                    val userDoc = firestore.collection("users").document(uid).get().await()
                    if (userDoc.exists()) {
                        val scoreFromDoc = userDoc.getLong("totalScore") ?: 0L
                        myScore = scoreFromDoc.toInt()
                        val nameFromDoc = userDoc.getString("username")
                        if (!nameFromDoc.isNullOrBlank()) {
                            myName = nameFromDoc
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Single user fetch skipped: ${e.message}")
                }

                val existingIndex = seedUsers.indexOfFirst { it.id == uid }
                if (existingIndex >= 0) {
                    seedUsers[existingIndex] = LeaderboardUser(uid, myName, myScore)
                } else {
                    seedUsers.add(LeaderboardUser(uid, myName, myScore))
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Current user resolution skipped: ${e.message}")
        }

        return seedUsers.sortedByDescending { it.totalScore }
    }

    fun errorShown() {
        _error.value = null
    }

    companion object {
        private const val TAG = "LeaderboardViewModel"
    }
}
