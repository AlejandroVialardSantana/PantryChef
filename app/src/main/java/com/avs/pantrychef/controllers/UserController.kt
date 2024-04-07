package com.avs.pantrychef.controllers

import com.avs.pantrychef.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Controlador que se encarga de gestionar las operaciones relacionadas con los usuarios como crear un usuario, añadir una receta a favoritos, obtener las recetas favoritas y eliminar una receta de favoritos
 *
 * @property db Instancia de la base de datos de Firebase
 */
class UserController {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Función que crea un usuario en la base de datos de Firebase
     *
     * @param user Usuario a crear
     * @param onSuccess Función que se ejecuta si la operación es exitosa
     * @param onFailure Función que se ejecuta si la operación falla
     */
    fun createUser(user: User, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("users").document(user.userId).set(user)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message.toString())
            }
    }

    /**
     * Función que añade una receta a la lista de recetas favoritas de un usuario
     *
     * @param recipeId Identificador de la receta
     * @param onSuccess Función que se ejecuta si la operación es exitosa
     * @param onFailure Función que se ejecuta si la operación falla
     */
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

    /**
     * Función que obtiene las recetas favoritas de un usuario
     *
     * @param onSuccess Función que se ejecuta si la operación es exitosa
     * @param onFailure Función que se ejecuta si la operación falla
     */
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

    /**
     * Función que elimina una receta de la lista de recetas favoritas de un usuario especificando el identificador de la receta
     *
     * @param recipeId Identificador de la receta
     * @param onSuccess Función que se ejecuta si la operación es exitosa
     * @param onFailure Función que se ejecuta si la operación falla
     */
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