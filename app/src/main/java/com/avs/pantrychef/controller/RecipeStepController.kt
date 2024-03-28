package com.avs.pantrychef.controller

import com.avs.pantrychef.model.Step
import com.google.firebase.firestore.FirebaseFirestore

class RecipeStepController {

    private val db = FirebaseFirestore.getInstance()

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