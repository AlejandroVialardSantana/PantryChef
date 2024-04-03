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

/**
 * Actividad para registrar un nuevo usuario.
 *
 * @property authController Controlador de autenticación.
 */
class SignUpActivity: AppCompatActivity() {

    private lateinit var authController: AuthController

    /**
     * Función que se ejecuta al crear la actividad.
     *
     * Se encarga de inicializar los elementos de la vista y de asignar los listeners a los botones
     * para realizar la acción de registro de un nuevo usuario.
     * O también para redirigir al usuario a la pantalla de inicio de sesión si presiona el enlace.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        authController = AuthController()

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

    /**
     * Función que valida los campos introducidos por el usuario al registrarse.
     * Comprueba si los campos de correo electrónico, nombre de usuario, contraseña y confirmar contraseña
     * están vacíos, si el formato del correo electrónico es correcto y si las contraseñas coinciden.
     *
     * @param email Correo electrónico introducido por el usuario.
     * @param userName Nombre de usuario introducido por el usuario.
     * @param password Contraseña introducida por el usuario.
     * @param confirmPassword Confirmación de la contraseña introducida por el usuario.
     * @return Booleano que indica si los campos son válidos o no.
     */
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

    /**
     * Función que se ejecuta cuando se ha registrado un nuevo usuario.
     * Muestra un mensaje de éxito y redirige al usuario a la pantalla home de la aplicación.
     */
    private fun onRegisterSuccess() {
        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

        val homeActivity = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(homeActivity)
        finish()
    }

    /**
     * Función que se ejecuta cuando no se ha podido registrar un nuevo usuario.
     * Muestra un mensaje de error.
     */
    private fun onRegisterFailure() {
        Toast.makeText(this, "Registro fallido", Toast.LENGTH_SHORT).show()
    }

}