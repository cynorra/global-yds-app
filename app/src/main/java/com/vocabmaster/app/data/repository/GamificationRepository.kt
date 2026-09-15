package com.vocabmaster.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class UserStreak(val current: Int, val longest: Int)

/**
 * Tracks the user's daily study streak in the same `users/{uid}` Firestore document that
 * already carries username/totalScore (see FirebaseExamRepositoryImpl). Not modeled as an
 * interface+impl pair like the other repositories since, like LeaderboardViewModel, it talks
 * to Firestore directly with no alternate backend planned.
 */
object GamificationRepository {

    // A streak is a single shared timeline per user, so "today"/"yesterday" must be a fixed
    // UTC calendar day (see FirebaseExamRepositoryImpl.ensureDailyExam for the same reasoning)
    // rather than the device's local day.
    private fun utcDateFormat() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun todayUtc(): String = utcDateFormat().format(Date())

    private fun yesterdayUtc(): String {
        val cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.add(java.util.Calendar.DATE, -1)
        return utcDateFormat().format(cal.time)
    }

    /**
     * Call once per genuine study action (exam submitted, word marked learned). Idempotent
     * within a single UTC day - calling it many times today only records the streak once.
     */
    suspend fun recordDailyActivity(firestore: FirebaseFirestore, uid: String) {
        val userDoc = firestore.collection("users").document(uid)
        val today = todayUtc()
        val yesterday = yesterdayUtc()
        try {
            firestore.runTransaction { txn ->
                val snapshot = txn.get(userDoc)
                val lastActive = snapshot.getString("lastActiveDate")
                if (lastActive == today) return@runTransaction null

                val currentStreak = (snapshot.getLong("currentStreak") ?: 0L).toInt()
                val longestStreak = (snapshot.getLong("longestStreak") ?: 0L).toInt()
                val newStreak = if (lastActive == yesterday) currentStreak + 1 else 1
                val newLongest = maxOf(longestStreak, newStreak)

                txn.set(
                    userDoc,
                    mapOf(
                        "lastActiveDate" to today,
                        "currentStreak" to newStreak,
                        "longestStreak" to newLongest
                    ),
                    SetOptions.merge()
                )
                null
            }.await()
        } catch (e: Exception) {
            Log.w(TAG, "recordDailyActivity failed", e)
        }
    }

    suspend fun getStreak(firestore: FirebaseFirestore, uid: String): UserStreak {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val today = todayUtc()
            val yesterday = yesterdayUtc()
            val lastActive = doc.getString("lastActiveDate")
            val storedStreak = (doc.getLong("currentStreak") ?: 0L).toInt()
            // A streak not extended today or yesterday is dead - show 0 rather than a stale
            // number from days ago (the Firestore value only resets forward on next activity).
            val current = if (lastActive == today || lastActive == yesterday) storedStreak else 0
            UserStreak(current = current, longest = (doc.getLong("longestStreak") ?: 0L).toInt())
        } catch (e: Exception) {
            Log.w(TAG, "getStreak failed", e)
            UserStreak(0, 0)
        }
    }

    private const val TAG = "GamificationRepo"
}
