package com.vocabmaster.app

import android.content.Intent
import android.net.Uri
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

        findViewById<TextView>(R.id.tvPrivacyPolicy).setOnClickListener {
            openUrl(getString(R.string.url_privacy_policy))
        }

        findViewById<TextView>(R.id.tvTerms).setOnClickListener {
            openUrl(getString(R.string.url_terms))
        }

        findViewById<TextView>(R.id.tvShareApp).setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_app_title))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_text))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_title)))
        }
    }

    private fun openUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (_: Exception) {}
    }
}
