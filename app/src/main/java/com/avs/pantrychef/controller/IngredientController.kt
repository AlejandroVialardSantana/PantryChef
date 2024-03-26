package com.avs.pantrychef.controller

import com.avs.pantrychef.model.Ingredient
import com.google.firebase.firestore.FirebaseFirestore

class IngredientController {

    private val db = FirebaseFirestore.getInstance()

    fun fetchIngredients(languageCode: String, onSuccess: (List<Ingredient>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("ingredients")
            .get()
            .addOnSuccessListener { result ->
                val ingredients = result.documents.mapNotNull { document ->
                    val nameField = "name_${languageCode}"
                    val measurementUnitField = "measurementUnit_${languageCode}"

                    val name = document.getString(nameField) ?: return@mapNotNull null
                    val measurementUnit = document.getString(measurementUnitField) ?: return@mapNotNull null
                    Ingredient(document.id, name, measurementUnit)
                }
                onSuccess(ingredients)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
