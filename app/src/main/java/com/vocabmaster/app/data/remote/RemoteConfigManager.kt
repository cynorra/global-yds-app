package com.vocabmaster.app.data.remote

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * A handful of server-controlled toggles, tunable from the Firebase console without an app
 * release. Defaults below are what ships if the console value is never set, so the app behaves
 * identically to before this existed until someone actually changes a value remotely.
 */
object RemoteConfigManager {

    private const val KEY_DAILY_EXAM_ENABLED = "daily_exam_enabled"
    private const val KEY_INTERSTITIAL_AD_ENABLED = "interstitial_ad_enabled"

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build()
            )
            setDefaultsAsync(
                mapOf(
                    KEY_DAILY_EXAM_ENABLED to true,
                    KEY_INTERSTITIAL_AD_ENABLED to true
                )
            )
        }
    }

    fun initialize() {
        remoteConfig.fetchAndActivate().addOnFailureListener { e ->
            Log.w("RemoteConfigManager", "fetchAndActivate failed, using defaults/cached values", e)
        }
    }

    /** Lets the daily-exam feature be paused server-side (e.g. if its content pipeline breaks). */
    val isDailyExamEnabled: Boolean
        get() = remoteConfig.getBoolean(KEY_DAILY_EXAM_ENABLED)

    /** Lets the post-exam interstitial be disabled remotely without an app release. */
    val isInterstitialAdEnabled: Boolean
        get() = remoteConfig.getBoolean(KEY_INTERSTITIAL_AD_ENABLED)
}
