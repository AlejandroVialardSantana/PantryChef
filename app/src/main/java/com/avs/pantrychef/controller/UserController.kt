package com.avs.pantrychef.controller

import com.avs.pantrychef.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class UserController {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun createUser(user: User, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("users").document(user.userId).set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message.toString())
            }
    }

    fun addRecipeToFavorites(recipeId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in")

        db.collection("users").document(userId).update("favoriteRecipes", FieldValue.arrayUnion(recipeId))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message.toString())
            }
    }

    fun getFavoriteRecipes(onSuccess: (List<String>) -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in")

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val favoriteRecipes = document.get("favoriteRecipes") as? List<String> ?: emptyList()
                onSuccess(favoriteRecipes)
            }
            .addOnFailureListener {
                onFailure(it.message.toString())
            }
    }

    fun removeRecipeFromFavorites(recipeId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onFailure("User not logged in")

        db.collection("users").document(userId).update("favoriteRecipes", FieldValue.arrayRemove(recipeId))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message.toString())
            }
    }
}