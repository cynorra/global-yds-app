package com.vocabmaster.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            null
        }
        findViewById<TextView>(R.id.tvVersion).text =
            getString(R.string.about_version, versionName ?: "1.0")
    }
}
