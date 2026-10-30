package com.vocabmaster.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.vocabmaster.app.data.local.AppPreferences

class GlobalYdsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val themeMode = AppPreferences.getThemeMode(this)
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}

