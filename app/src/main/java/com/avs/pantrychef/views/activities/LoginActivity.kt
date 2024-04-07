package com.avs.pantrychef.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.avs.pantrychef.R
import com.avs.pantrychef.controllers.AuthController

/**
 * Actividad que permite al usuario iniciar sesión en la aplicación.
 *
 * @property authController Controlador de autenticación.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var authController: AuthController

    /**
     * Función que se ejecuta al crear la actividad.
     *
     * Se encarga de inicializar los elementos de la vista y de asignar los listeners a los botones
     * que derivan a otras actividades.
     *
     * @param savedInstanceState Instancia guardada de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authController = AuthController()

        val backIcon: ImageView = findViewById(R.id.backIcon)
        val loginButton: Button = findViewById(R.id.loginPageButton)
        val signUpLink: TextView = findViewById(R.id.signUpLink)
        val forgotPasswordLink: TextView = findViewById(R.id.changePasswordLink)

        backIcon.setOnClickListener {
            finish()
        }

        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailLoginInput).text.toString()
            val password = findViewById<EditText>(R.id.passwordLoginInput).text.toString()

            if (validateLogin(email, password)) {
                authController.logIn(email, password, ::onLoginSuccess, ::onLoginFailure)
            }
        }

        signUpLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Función que valida los campos de correo electrónico y contraseña.
     *
     * @param email Correo electrónico introducido por el usuario.
     * @param password Contraseña introducida por el usuario.
     * @return Booleano que indica si los campos son válidos.
     */
    private fun validateLogin(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "El campo de correo electrónico está vacío.", Toast.LENGTH_SHORT).show()
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Introduce un correo electrónico válido.", Toast.LENGTH_SHORT).show()
            return false
        } else if (password.isEmpty()) {
            Toast.makeText(this, "El campo de contraseña está vacío.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Función que se ejecuta al iniciar sesión correctamente.
     *
     * Muestra un mensaje de éxito y redirige al usuario a la actividad principal.
     */
    private fun onLoginSuccess() {
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

        val homeActivity = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(homeActivity)
        finish()
    }

    /**
     * Función que se ejecuta al fallar el inicio de sesión.
     *
     * Muestra un mensaje de error.
     */
    private fun onLoginFailure() {
        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
    }
}
