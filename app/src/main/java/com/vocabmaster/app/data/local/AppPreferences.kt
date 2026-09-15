package com.vocabmaster.app.data.local

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val PREFS_NAME = "vocabmaster_prefs"
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    private const val KEY_REMINDER_ENABLED = "reminder_enabled"
    private const val KEY_PREMIUM_CACHED = "premium_cached"

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @JvmStatic
    fun isOnboardingComplete(context: Context): Boolean =
        prefs(context).getBoolean(KEY_ONBOARDING_COMPLETE, false)

    @JvmStatic
    fun setOnboardingComplete(context: Context) {
        prefs(context).edit().putBoolean(KEY_ONBOARDING_COMPLETE, true).apply()
    }

    @JvmStatic
    fun isReminderEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_REMINDER_ENABLED, false)

    @JvmStatic
    fun setReminderEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
    }

    // Local cache of the Play Billing entitlement, so gating UI (bottom nav, exam list) has an
    // instant answer on cold start instead of flickering while BillingManager reconnects.
    // BillingManager (the source of truth) overwrites this every time it reconnects.
    @JvmStatic
    fun isPremiumCached(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PREMIUM_CACHED, false)

    @JvmStatic
    fun setPremiumCached(context: Context, premium: Boolean) {
        prefs(context).edit().putBoolean(KEY_PREMIUM_CACHED, premium).apply()
    }
}
