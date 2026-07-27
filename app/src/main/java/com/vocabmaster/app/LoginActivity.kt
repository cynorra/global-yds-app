package com.vocabmaster.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.vocabmaster.app.ui.viewmodels.LoginViewModel
import com.shashank.sony.fancytoastlib.FancyToast

class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var btnLogin: CircularProgressButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            // For now, hardcoded dummy credentials to trigger mock repository
            loginViewModel.login("test@test.com", "123456")
        }

        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                btnLogin.startAnimation()
            } else {
                btnLogin.revertAnimation()
            }
        }

        loginViewModel.loginStatus.observe(this) { isSuccess ->
            if (isSuccess) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            } else {
                FancyToast.makeText(this, getString(R.string.login_error), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
            }
        }
    }
}
