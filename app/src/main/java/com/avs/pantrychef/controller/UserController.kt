package com.avs.pantrychef.controller

import com.avs.pantrychef.model.User
import com.google.firebase.firestore.FirebaseFirestore

class UserController {

    private val db = FirebaseFirestore.getInstance()

    fun createUser(user: User, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("users").document(user.userId).set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message.toString())
            }
    }
}