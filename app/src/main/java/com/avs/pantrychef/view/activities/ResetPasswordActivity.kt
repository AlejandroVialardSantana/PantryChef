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

/**
 * Actividad para restablecer la contraseña del usuario.
 *
 * @property authController Controlador de autenticación.
 */
class ResetPasswordActivity: AppCompatActivity() {

    private lateinit var authController: AuthController

    /**
     * Función que se ejecuta al crear la actividad.
     *
     * Se encarga de inicializar los elementos de la vista y de asignar los listeners a los botones
     * para realizar la acción de restablecer la contraseña.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        authController = AuthController()

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

    /**
     * Función que valida el correo electrónico introducido por el usuario.
     * Comprueba si el campo está vacío y si el formato del correo electrónico es correcto.
     *
     * @param email Correo electrónico introducido por el usuario.
     * @return Booleano que indica si el correo electrónico es válido o no.
     */
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

    /**
     * Función que se ejecuta cuando se ha enviado el correo electrónico para restablecer la contraseña.
     * Muestra un mensaje de éxito y redirige al usuario a la pantalla de inicio de sesión.
     */
    private fun onResetSuccess() {
        Toast.makeText(this, "Se ha enviado un correo electrónico para restablecer la contraseña.", Toast.LENGTH_SHORT).show()

        val loginActivity = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(loginActivity)
        finish()
    }

    /**
     * Función que se ejecuta cuando no se ha podido enviar el correo electrónico para restablecer la contraseña.
     * Muestra un mensaje de error.
     */
    private fun onResetFailure() {
        Toast.makeText(this, "No se ha podido enviar el correo electrónico para restablecer la contraseña.", Toast.LENGTH_SHORT).show()
    }
}