package com.vocabmaster.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputEditText
import com.vocabmaster.app.domain.repository.AuthResult
import com.vocabmaster.app.ui.viewmodels.LoginViewModel
import com.shashank.sony.fancytoastlib.FancyToast

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var btnLogin: CircularProgressButton
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)

        btnLogin.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()
            if (email.isEmpty() || password.isEmpty()) {
                FancyToast.makeText(this, getString(R.string.auth_error_empty_fields), FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
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
