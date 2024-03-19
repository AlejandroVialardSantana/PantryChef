package com.avs.pantrychef.controller

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class AuthController(private val context: Context) {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

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

    fun signUp(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AuthController", "createUserWithEmail:success")
                onSuccess()
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

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logOut() {
        auth.signOut()
    }
}

