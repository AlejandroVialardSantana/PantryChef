package com.avs.pantrychef.controller

import android.util.Log
import com.avs.pantrychef.model.Ingredient
import com.avs.pantrychef.model.Recipe
import com.avs.pantrychef.model.RecipeIngredient
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

/**
 * RecipeController es responsable de manejar las operaciones relacionadas con los documentos de la colección "recipes" en Firestore.
 *
 * @property db Instancia de FirebaseFirestore.
 */
class RecipeController {

    private val db = FirebaseFirestore.getInstance()

    /**
     * fetchRecipesByIngredients recupera las recetas que contienen los ingredientes especificados por el usuario.
     *
     * @param ingredientIds Arreglo de IDs de ingredientes.
     * @param languageCode Código de idioma para obtener los campos de idioma correspondientes.
     * @param onSuccess Función de retorno que se ejecuta si la operación es exitosa.
     * @param onFailure Función de retorno que se ejecuta si la operación falla.
     */
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

    /**
     * fetchRecipesByIds recupera las recetas por sus IDs.
     *
     * @param recipeIds Lista de IDs de recetas.
     * @param languageCode Código de idioma para obtener los campos de idioma correspondientes.
     * @param onSuccess Función de retorno que se ejecuta si la operación es exitosa.
     * @param onFailure Función de retorno que se ejecuta si la operación falla.
     */
    fun fetchRecipesByIds(recipeIds: List<String?>, languageCode: String, onSuccess: (List<Recipe>) -> Unit, onFailure: (Exception) -> Unit) {
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

    /**
     * getRecipeById obtiene una receta única por su ID.
     *
     * @param recipeId ID de la receta.
     * @param languageCode Código de idioma para obtener los campos de idioma correspondientes.
     * @param onSuccess Función de retorno que se ejecuta si la operación es exitosa.
     * @param onFailure Función de retorno que se ejecuta si la operación falla.
     */
    fun getRecipeById(recipeId: String, languageCode: String, onSuccess: (Recipe) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("recipes")
            .document(recipeId)
            .get()
            .addOnSuccessListener { document ->
                try {
                    val titleField = "title_$languageCode"
                    val difficultyField = "difficulty_$languageCode"
                    val typeField = "type_$languageCode"

                    val recipe = Recipe(
                        id = document.id,
                        title = document.getString(titleField) ?: "",
                        difficulty = document.getString(difficultyField) ?: "",
                        type = document.getString(typeField) ?: "",
                        ingredients = document.get("ingredients") as? List<String> ?: emptyList(),
                        steps = document.get("steps") as? List<String> ?: emptyList(),
                        preparationTime = document.getLong("preparationTime")?.toInt() ?: 0
                    )
                    onSuccess(recipe)
                } catch (e: Exception) {
                    onFailure(e)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * getRecipeIngredientsByIds obtiene los ingredientes de una receta por sus IDs.
     *
     * @param recipeIngredientIds Lista de IDs de ingredientes de la receta.
     * @param languageCode Código de idioma para obtener los campos de idioma correspondientes.
     * @param onSuccess Función de retorno que se ejecuta si la operación es exitosa.
     * @param onFailure Función de retorno que se ejecuta si la operación falla.
     */
    private fun getRecipeIngredientsByIds(recipeIngredientIds: List<String>, languageCode: String, onSuccess: (List<RecipeIngredient>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("recipe_ingredients")
            .whereIn(FieldPath.documentId(), recipeIngredientIds)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipeIngredients = querySnapshot.documents.mapNotNull { it.toObject(RecipeIngredient::class.java) }
                onSuccess(recipeIngredients)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * getIngredientsDetails obtiene los detalles de los ingredientes por sus IDs.
     *
     * @param ids Lista de IDs de ingredientes.
     * @param languageCode Código de idioma para obtener los campos de idioma correspondientes.
     * @param onSuccess Función de retorno que se ejecuta si la operación es exitosa.
     * @param onFailure Función de retorno que se ejecuta si la operación falla.
     */
    private fun getIngredientsDetails(ids: List<String>, languageCode: String, onSuccess: (List<Ingredient>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("ingredients")
            .whereIn(FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val ingredients = querySnapshot.documents.mapNotNull { document ->
                    try {
                        val nameField = "name_$languageCode"
                        val measurementUnitField = "measurementUnit_$languageCode"
                        Ingredient(
                            id = document.id,
                            name = document.getString(nameField) ?: "",
                            measurementUnit = document.getString(measurementUnitField) ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
                onSuccess(ingredients)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    /**
     * getRecipeWithIngredientsById obtiene una receta con sus ingredientes por su ID.
     *
     * @param recipeId ID de la receta.
     * @param languageCode Código de idioma para obtener los campos de idioma correspondientes.
     * @param onSuccess Función de retorno que se ejecuta si la operación es exitosa.
     * @param onFailure Función de retorno que se ejecuta si la operación falla.
     */
    fun getRecipeWithIngredientsById(recipeId: String, languageCode: String, onSuccess: (Recipe, List<Ingredient>) -> Unit, onFailure: (Exception) -> Unit) {
        getRecipeById(recipeId, languageCode, { recipe ->
            // Verifica si la receta tiene ingredientes
            if (recipe.ingredients.isNotEmpty()) {
                Log.d("RecipeController", "Recipe has ingredients: ${recipe.ingredients}")

                getRecipeIngredientsByIds(recipe.ingredients, languageCode, { recipeIngredients ->
                    // Extrae los IDs de ingredientes de 'recipeIngredients'
                    val ingredientIds = recipeIngredients.mapNotNull { it.ingredientId }

                    // Obtiene los detalles de los ingredientes
                    if (ingredientIds.isNotEmpty()) {
                        getIngredientsDetails(ingredientIds, languageCode, { ingredients ->
                            onSuccess(recipe, ingredients)
                        }, onFailure)
                    } else {
                        // Manejar el caso en que no hay ingredientIds
                        onSuccess(recipe, emptyList())
                    }
                }, onFailure)
            } else {
                // Manejar el caso en que la receta no tiene ingredientes listados
                Log.d("RecipeController", "Recipe has no ingredients")
                onSuccess(recipe, emptyList())
            }
        }, onFailure)
    }

    fun getRecipeQuantity(recipeId: String, ingredientId: String, onSuccess: (Int) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("recipe_ingredients")
            .whereEqualTo("recipeId", recipeId)
            .whereEqualTo("ingredientId", ingredientId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    onSuccess(0)
                } else {
                    val recipeIngredient = querySnapshot.documents.first().toObject(RecipeIngredient::class.java)
                    onSuccess(recipeIngredient?.quantity?.toInt() ?: 0)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
