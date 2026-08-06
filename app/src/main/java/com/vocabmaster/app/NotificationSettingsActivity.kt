package com.vocabmaster.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.vocabmaster.app.data.local.AppPreferences
import com.vocabmaster.app.notifications.ReminderScheduler

class NotificationSettingsActivity : AppCompatActivity() {

    private lateinit var switchReminder: SwitchMaterial

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                AppPreferences.setReminderEnabled(this, true)
                ReminderScheduler.schedule(this)
            } else {
                switchReminder.isChecked = false
                Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.notifications_permission_denied),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_settings)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        switchReminder = findViewById(R.id.switchReminder)
        switchReminder.isChecked = AppPreferences.isReminderEnabled(this)

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableReminder()
            } else {
                AppPreferences.setReminderEnabled(this, false)
                ReminderScheduler.cancel(this)
            }
        }
    }

    private fun enableReminder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        AppPreferences.setReminderEnabled(this, true)
        ReminderScheduler.schedule(this)
    }
}
