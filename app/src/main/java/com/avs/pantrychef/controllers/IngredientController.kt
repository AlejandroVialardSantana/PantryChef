package com.avs.pantrychef.controllers

import com.avs.pantrychef.models.Ingredient
import com.google.firebase.firestore.FirebaseFirestore

/**
 * IngredientController es responsable de manejar las operaciones relacionadas con lson documentos de la colección "ingredients" en Firestore.
 *
 * @property db Instancia de FirebaseFirestore.
 */
class IngredientController {

    private val db = FirebaseFirestore.getInstance()

    /**
     * fetchIngredients recupera los ingredientes de Firestore y maneja el cambio de idioma
     * obteniendo los campos de idioma correspondientes.
     *
     * @param languageCode Código de idioma para obtener los campos de idioma correspondientes.
     * @param onSuccess Función de retorno que se ejecuta si la operación es exitosa.
     * @param onFailure Función de retorno que se ejecuta si la operación falla.
     */
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
