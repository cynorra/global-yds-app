package com.vocabmaster.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.vocabmaster.app.data.local.AppPreferences

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { false }

        val destination = when {
            FirebaseAuth.getInstance().currentUser != null -> MainActivity::class.java
            AppPreferences.isOnboardingComplete(this) -> LoginActivity::class.java
            else -> OnboardingActivity::class.java
        }

        startActivity(Intent(this, destination))
        finish()
    }
}
