package com.vocabmaster.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputEditText
import com.vocabmaster.app.domain.repository.AuthResult
import com.vocabmaster.app.ui.viewmodels.RegisterViewModel
import com.shashank.sony.fancytoastlib.FancyToast

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel: RegisterViewModel by viewModels()
    private lateinit var btnRegister: CircularProgressButton
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById(R.id.btnRegister)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)

        btnRegister.setOnClickListener {
            val email = etEmail.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString().orEmpty()
            val confirmPassword = etConfirmPassword.text?.toString().orEmpty()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                FancyToast.makeText(this, getString(R.string.auth_error_empty_fields), FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                FancyToast.makeText(this, getString(R.string.auth_error_password_mismatch), FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show()
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
