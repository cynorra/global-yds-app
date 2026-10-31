package com.vocabmaster.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.vocabmaster.app.data.local.AppPreferences

class GlobalYdsApp : Application() {

    companion object {
        lateinit var instance: GlobalYdsApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val themeMode = AppPreferences.getThemeMode(this)
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }
}

