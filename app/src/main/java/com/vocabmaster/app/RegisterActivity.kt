package com.vocabmaster.app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vocabmaster.app.domain.repository.AuthResult
import com.vocabmaster.app.ui.viewmodels.RegisterViewModel
import com.shashank.sony.fancytoastlib.FancyToast

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var btnRegister: CircularProgressButton
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById(R.id.btnRegister)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegister.setOnClickListener {
            tilEmail.error = null
            tilPassword.error = null
            tilConfirmPassword.error = null

            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()
            val confirmPassword = etConfirmPassword.text?.toString().orEmpty()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                FancyToast.makeText(this, getString(R.string.auth_error_empty_fields), FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = getString(R.string.auth_error_invalid_email)
                return@setOnClickListener
            }
            if (password.length < 6) {
                tilPassword.error = getString(R.string.auth_error_weak_password)
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                tilConfirmPassword.error = getString(R.string.auth_error_password_mismatch)
                return@setOnClickListener
            }
            registerViewModel.register(email, password)
        }

        findViewById<android.widget.TextView>(R.id.tvGoLogin).setOnClickListener {
            finish()
        }

        registerViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                btnRegister.startAnimation()
            } else {
                btnRegister.revertAnimation()
            }
        }

        registerViewModel.authResult.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    finish()
                }
                is AuthResult.Error -> {
                    FancyToast.makeText(this, result.message, FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
                }
                null -> Unit
            }
        }
    }
}
