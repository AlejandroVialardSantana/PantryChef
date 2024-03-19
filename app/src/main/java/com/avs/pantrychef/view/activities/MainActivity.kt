package com.avs.pantrychef.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.AuthController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authController = AuthController(this)

        if (authController.isUserLoggedIn()) {
            navigateToHome()
        } else {
            setupLoginAndSignUpButtons()
        }
    }

    private fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(homeIntent)
        finish()
    }

    private fun setupLoginAndSignUpButtons() {
        val loginButton: Button = findViewById(R.id.mainLoginButton)
        val signUpButton: Button = findViewById(R.id.mainRegisterButton)

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}