package com.avs.pantrychef.controllers

import com.avs.pantrychef.models.Step
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Controlador para operaciones relacionadas con los pasos de una receta del documento "recipe_steps" en Firestore.
 *
 * @property db Instancia de Firestore
 */
class RecipeStepController {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Obtiene los pasos mediante el Id de la receta y el código de idioma para cambiar la descripción.
     *
     * @param recipeId Id de la receta
     * @param languageCode Código de idioma
     * @param onSuccess Función a ejecutar en caso de éxito
     * @param onFailure Función a ejecutar en caso de fallo
     */
    fun fetchStepsByRecipeId(recipeId: String, languageCode: String, onSuccess: (List<Step>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("recipe_steps")
            .whereEqualTo("recipeId", recipeId)
            .orderBy("order")
            .get()
            .addOnSuccessListener { documents ->
                val steps = documents.map { document ->
                    val descriptionField = "description_$languageCode"
                    Step(
                        id = document.id,
                        description = document.getString(descriptionField) ?: "",
                        order = document.getLong("order")?.toInt() ?: 0,
                        recipeId = document.getString("recipeId") ?: "",
                        time = document.getLong("time")?.toInt() ?: null
                    )
                }
                onSuccess(steps)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}