package com.avs.pantrychef.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.avs.pantrychef.model.Ingredient
import java.io.File
import java.io.IOException

class ShoppingListHelper {
    /**
     * Crea un archivo de texto con la lista de compra y lo guarda en el directorio de archivos de la app.
     * Retorna la URI del archivo creado.
     */
    fun createShoppingListFile(
        ingredients: List<Ingredient>,
        userIngredientsIds: List<String>,
        context: Context
    ): Uri? {
        // Generar el contenido del archivo marcando los ingredientes que el usuario ya tiene
        val shoppingListContent = ingredients.joinToString(separator = "\n") { ingredient ->
            if (userIngredientsIds.contains(ingredient.id)) {
                "- [X] ${ingredient.name}" // Marcar el ingrediente como ya poseído
            } else {
                "- [ ] ${ingredient.name}" // Ingrediente no poseído
            }
        }

        Log.d("IngredientsListFragment", "Contenido de la lista de compras: $shoppingListContent")

        try {
            val file = File(context.filesDir, "shoppingList.txt")
            file.writeText(shoppingListContent)
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: IOException) {
            Log.e("IngredientsListFragment", "Error al crear el archivo de la lista de compras", e)
            return null
        }
    }

    /**
     * Comparte el archivo de la lista de compras con otras apps.
     */
    fun shareShoppingListFile(fileUri: Uri, context: Context) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "text/plain"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Shopping List"))
    }
}