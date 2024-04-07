package com.avs.pantrychef.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.avs.pantrychef.models.IngredientWithQuantity
import java.io.File
import java.io.IOException

class ShoppingListHelper {
    /**
     * Crea un archivo de texto con la lista de compra y lo guarda en el directorio de archivos de la app.
     * Retorna la URI del archivo creado.
     */
    fun createShoppingListFile(
        ingredientsWithQuantity: List<IngredientWithQuantity>,
        selectedIngredientIds: List<String>,
        context: Context
    ): Uri? {
        val shoppingListContent = ingredientsWithQuantity.joinToString(separator = "\n") { item ->
            val checkMark = if (selectedIngredientIds.contains(item.ingredient.id)) "[X]" else "[ ]"
            "$checkMark ${item.ingredient.name} - ${item.quantity} ${item.ingredient.measurementUnit}"
        }

        try {
            val file = File(context.filesDir, "shoppingList.txt")
            file.writeText(shoppingListContent)
            return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: IOException) {
            Log.e("ShoppingListHelper", "Error creating shopping list file", e)
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