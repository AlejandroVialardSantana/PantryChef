package com.avs.pantrychef.controller

import android.content.Context
import android.util.Log
import com.avs.pantrychef.model.User
import com.google.firebase.auth.FirebaseAuth

/**
 * AuthController es responsable de manejar la autenticación de los usuarios.
 * Se encarga de iniciar sesión, cerrar sesión, registrar un nuevo usuario y restablecer la contraseña.
 *
 * @property auth Instancia de FirebaseAuth.
 * @property userController Instancia de UserController.
 */
class AuthController {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var userController: UserController = UserController()

    fun logIn(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AuthController", "signInWithEmail:success")
                onSuccess()
            } else {
                Log.w("AuthController", "signInWithEmail:failure", task.exception)
                onFailure()
            }
        }
    }

    fun signUp(email: String, username: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AuthController", "createUserWithEmail:success")

                val userId = auth.currentUser?.uid ?: ""

                val newUser = User(userId, username, email, listOf())

                userController.createUser(newUser, {
                    onSuccess()
                }, {
                    onFailure()
                })
            } else {
                Log.w("AuthController", "createUserWithEmail:failure", task.exception)
                onFailure()
            }
        }
    }

    fun resetPassword(email: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AuthController", "sendPasswordResetEmail:success")
                onSuccess()
            } else {
                Log.w("AuthController", "sendPasswordResetEmail:failure", task.exception)
                onFailure()
            }
        }
    }

    /**
     * Verifica si el usuario está logueado.
     *
     * @return true si el usuario está logueado, false en caso contrario.
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logOut() {
        auth.signOut()
    }
}

