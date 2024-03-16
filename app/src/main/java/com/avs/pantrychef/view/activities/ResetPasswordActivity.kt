package com.avs.pantrychef.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.AuthController

class ResetPasswordActivity: AppCompatActivity() {

    private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        authController = AuthController(this)

        val backIcon: ImageView = findViewById(R.id.backIcon)
        val changePasswordButton: Button = findViewById(R.id.changePasswordButton)

        backIcon.setOnClickListener {
            finish()
        }

        changePasswordButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailResetInput).text.toString()

            if (validateReset(email)) {
                authController.resetPassword(email, ::onResetSuccess, ::onResetFailure)
            }
        }
    }

    private fun validateReset(email: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "El campo de correo electrónico está vacío.", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Introduce un correo electrónico válido.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun onResetSuccess() {
        Toast.makeText(this, "Se ha enviado un correo electrónico para restablecer la contraseña.", Toast.LENGTH_SHORT).show()

        val loginActivity = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(loginActivity)
        finish()
    }

    private fun onResetFailure() {
        Toast.makeText(this, "No se ha podido enviar el correo electrónico para restablecer la contraseña.", Toast.LENGTH_SHORT).show()
    }
}