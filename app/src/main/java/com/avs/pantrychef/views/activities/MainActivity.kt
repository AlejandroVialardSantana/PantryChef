package com.avs.pantrychef.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.avs.pantrychef.R
import com.avs.pantrychef.controllers.AuthController

/**
 * Actividad principal de la aplicación, es la que se muestra al iniciar la aplicación.
 * Muestra dos botones, uno para iniciar sesión y otro para registrarse.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Método que se ejecuta al crear la actividad.
     * Verifica si el usuario ya está logueado, si es así, lo redirige a la actividad Home.
     * Si no está logueado, muestra los botones de login y registro.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val authController = AuthController()

        if (authController.isUserLoggedIn()) {
            navigateToHome()
        } else {
            setupLoginAndSignUpButtons()
        }
    }

    /**
     * Método que redirige al usuario a la actividad Home.
     */
    private fun navigateToHome() {
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(homeIntent)
        finish()
    }

    /**
     * Método que configura los botones de login y registro.
     */
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