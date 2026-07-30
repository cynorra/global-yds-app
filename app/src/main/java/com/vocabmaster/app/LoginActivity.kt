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
import com.vocabmaster.app.ui.viewmodels.LoginViewModel
import com.shashank.sony.fancytoastlib.FancyToast

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var btnLogin: CircularProgressButton
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnLogin.setOnClickListener {
            tilEmail.error = null
            tilPassword.error = null

            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()

            if (email.isEmpty() || password.isEmpty()) {
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
            loginViewModel.login(email, password)
        }

        findViewById<android.widget.TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                btnLogin.startAnimation()
            } else {
                btnLogin.revertAnimation()
            }
        }

        loginViewModel.authResult.observe(this) { result ->
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
