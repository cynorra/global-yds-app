package com.vocabmaster.app.data.local

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val PREFS_NAME = "vocabmaster_prefs"
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    private const val KEY_REMINDER_ENABLED = "reminder_enabled"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_TARGET_EXAM = "target_exam"

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @JvmStatic
    fun getThemeMode(context: Context): Int =
        prefs(context).getInt(KEY_THEME_MODE, androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

    @JvmStatic
    fun setThemeMode(context: Context, mode: Int) {
        prefs(context).edit().putInt(KEY_THEME_MODE, mode).apply()
    }

    @JvmStatic
    fun getTargetExam(context: Context): String =
        prefs(context).getString(KEY_TARGET_EXAM, "ALL") ?: "ALL"

    @JvmStatic
    fun setTargetExam(context: Context, examCode: String) {
        prefs(context).edit().putString(KEY_TARGET_EXAM, examCode).apply()
    }

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
}
