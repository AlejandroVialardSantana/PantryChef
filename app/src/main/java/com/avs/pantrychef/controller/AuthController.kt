package com.avs.pantrychef.controller

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class AuthController(private val context: Context) {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signIn(email: String, password: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
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
}

