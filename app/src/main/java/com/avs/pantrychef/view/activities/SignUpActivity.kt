package com.avs.pantrychef.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.AuthController

class SignUpActivity: AppCompatActivity() {

    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        authController = AuthController(this)

        val backIcon: ImageView = findViewById(R.id.backIcon)
        val registerButton: Button = findViewById(R.id.registerPageButton)
        val loginLink: TextView = findViewById(R.id.loginLink)

        backIcon.setOnClickListener {
            finish()
        }

        registerButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailRegisterInput).text.toString()
            val userName = findViewById<EditText>(R.id.userNameRegisterInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordRegisterInput).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.confirmPasswordInput).text.toString()

            if (validateRegister(email, userName, password, confirmPassword)) {
                authController.signUp(email, userName, password, ::onRegisterSuccess, ::onRegisterFailure)
            }
        }

        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateRegister(email: String, userName: String, password: String, confirmPassword: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "El campo de correo electrónico está vacío.", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Introduce un correo electrónico válido.", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.isEmpty()) {
            Toast.makeText(this, "El campo de contraseña está vacío.", Toast.LENGTH_SHORT).show()
            return false
        } else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "El campo de confirmar contraseña está vacío.", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (userName.isEmpty()) {
            Toast.makeText(this, "El campo de nombre de usuario está vacío.", Toast.LENGTH_SHORT).show()
            return false
        } else if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onRegisterSuccess() {
        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

        val homeActivity = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(homeActivity)
        finish()
    }

    private fun onRegisterFailure() {
        Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()
    }

}