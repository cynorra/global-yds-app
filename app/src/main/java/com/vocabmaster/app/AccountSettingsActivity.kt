package com.vocabmaster.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccountSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val email = user?.email
        val root = findViewById<View>(android.R.id.content)

        findViewById<TextView>(R.id.tvEmail).text = email.orEmpty()
        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val btnChangePassword = findViewById<MaterialButton>(R.id.btnChangePassword)
        val btnDeleteAccount = findViewById<MaterialButton>(R.id.btnDeleteAccount)
        val tvWebDeleteInfo = findViewById<TextView>(R.id.tvWebDeleteInfo)

        btnChangePassword.setOnClickListener {
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

        tvWebDeleteInfo.setOnClickListener {
            val url = getString(R.string.url_delete_account)
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {
                Snackbar.make(root, url, Snackbar.LENGTH_LONG).show()
            }
        }

        btnDeleteAccount.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.account_delete_dialog_title))
                .setMessage(getString(R.string.account_delete_dialog_message))
                .setPositiveButton(getString(R.string.account_delete_confirm)) { _, _ ->
                    executeAccountDeletion(auth, root, btnChangePassword, btnDeleteAccount)
                }
                .setNegativeButton(getString(R.string.account_delete_cancel), null)
                .show()
        }
    }

    private fun executeAccountDeletion(
        auth: FirebaseAuth,
        root: View,
        btnChangePassword: MaterialButton,
        btnDeleteAccount: MaterialButton
    ) {
        val user = auth.currentUser ?: return
        val uid = user.uid

        btnChangePassword.isEnabled = false
        btnDeleteAccount.isEnabled = false

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Delete Firestore records
                    val firestore = FirebaseFirestore.getInstance()
                    val userDoc = firestore.collection("users").document(uid)

                    try {
                        val attempts = userDoc.collection("examAttempts").get().await()
                        for (doc in attempts.documents) {
                            doc.reference.delete().await()
                        }
                    } catch (_: Exception) {}

                    try {
                        val mistakes = userDoc.collection("mistakes").get().await()
                        for (doc in mistakes.documents) {
                            doc.reference.delete().await()
                        }
                    } catch (_: Exception) {}

                    try {
                        userDoc.delete().await()
                    } catch (_: Exception) {}

                    // Delete Firebase Auth user
                    user.delete().await()
                }

                if (isFinishing || isDestroyed) return@launch
                auth.signOut()

                val intent = Intent(this@AccountSettingsActivity, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()

            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                btnChangePassword.isEnabled = true
                btnDeleteAccount.isEnabled = true
                Snackbar.make(root, getString(R.string.account_delete_reauth_required), Snackbar.LENGTH_LONG).show()
            } catch (e: Exception) {
                btnChangePassword.isEnabled = true
                btnDeleteAccount.isEnabled = true
                Snackbar.make(root, e.localizedMessage ?: getString(R.string.error_generic_title), Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
