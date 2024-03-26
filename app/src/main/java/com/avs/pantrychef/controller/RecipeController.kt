package com.avs.pantrychef.controller

import com.avs.pantrychef.model.Recipe
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class RecipeController {

    private val db = FirebaseFirestore.getInstance()

    fun fetchRecipesByIngredients(ingredientIds: Array<String>, languageCode: String, onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {

        if (ingredientIds.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        db.collection("recipe_ingredients")
            .whereIn("ingredientId", ingredientIds.toList())
            .get()
            .addOnSuccessListener { documents ->
                val recipeIds = documents.map { it.getString("recipeId") }.distinct()
                fetchRecipesByIds(recipeIds, languageCode, onSuccess, onFailure)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun fetchRecipesByIds(recipeIds: List<String?>, languageCode: String, onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {
        if (recipeIds.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        db.collection("recipes")
            .whereIn(FieldPath.documentId(), recipeIds)
            .get()
            .addOnSuccessListener { documents ->
                val recipes = documents.mapNotNull { document ->
                    try {
                        val titleField = "title_$languageCode"
                        val difficultyField = "difficulty_$languageCode"
                        val typeField = "type_$languageCode"

                        Recipe(
                            id = document.id,
                            title = document.getString(titleField) ?: "",
                            difficulty = document.getString(difficultyField) ?: "",
                            type = document.getString(typeField) ?: "",
                            preparationTime = document.getLong("preparationTime")?.toInt() ?: 0
                        )
                    } catch (e: Exception) {
                        null // En caso de error, retorna null para este documento.
                    }
                }
                onSuccess(recipes)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
