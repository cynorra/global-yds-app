package com.vocabmaster.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class AccountSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email

        findViewById<TextView>(R.id.tvEmail).text = email.orEmpty()
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<MaterialButton>(R.id.btnChangePassword).setOnClickListener {
            val root = findViewById<android.view.View>(android.R.id.content)
            if (email.isNullOrEmpty()) return@setOnClickListener
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    if (isFinishing || isDestroyed) return@addOnSuccessListener
                    Snackbar.make(root, getString(R.string.account_change_password_sent), Snackbar.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    if (isFinishing || isDestroyed) return@addOnFailureListener
                    Snackbar.make(root, getString(R.string.error_generic_title), Snackbar.LENGTH_LONG).show()
                }
        }
    }
}
