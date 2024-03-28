package com.avs.pantrychef.view.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.RecipeController
import com.avs.pantrychef.model.Ingredient
import com.avs.pantrychef.view.adapters.IngredientAdapter
import com.avs.pantrychef.view.adapters.IngredientListAdapter
import java.io.File
import java.io.IOException
import java.util.Locale

class IngredientsListFragment: Fragment() {

    private val args: IngredientsListFragmentArgs by navArgs()
    private lateinit var ingredientsAdapter: IngredientListAdapter
    private val recipeController = RecipeController()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredients_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languageCode = Locale.getDefault().language
        val recipeId = args.recipeId
        val ingredientsIds = args.ingredientsIds.toList()

        recipeController.getRecipeWithIngredientsById(
            recipeId,
            languageCode,
            { recipe, ingredients ->
                Log.d("IngredientsList", "Ingredientes: $ingredients")
                activity?.runOnUiThread {
                    // Actualizar la UI con los detalles de la receta
                    view.findViewById<TextView>(R.id.tvRecipeName).text = recipe.title
                    view.findViewById<TextView>(R.id.tvRecipeTime).text =
                        "Tiempo aprox: ${recipe.preparationTime} min"
                    view.findViewById<TextView>(R.id.tvRecipeDifficulty).text =
                        "Dificultad: ${recipe.difficulty}"

                    // Configurar el RecyclerView con los ingredientes
                    setupRecyclerView(ingredients, ingredientsIds)
                }
            },
            { exception ->
                Log.e("IngredientsListFragment", "Error fetching recipe details", exception)
            })

        view.findViewById<Button>(R.id.btnMakeShoppingList).setOnClickListener {
            // TODO: Implementar lógica para hacer la lista de compra
            val selectedIngredients = ingredientsAdapter.ingredientsList
            val selectedIngredientsIds = ingredientsAdapter.selectedIngredientsIds

            val fileUri = createShoppingListFile(
                selectedIngredients,
                selectedIngredientsIds,
                requireContext()
            )
            if (fileUri != null) {
                shareShoppingListFile(fileUri, requireContext())
            }
        }

        view.findViewById<Button>(R.id.btnStartCooking).setOnClickListener {
            val action = IngredientsListFragmentDirections.actionIngredientsListFragmentToRecipeStepFragment(recipeId)
            view?.findNavController()?.navigate(action)
        }
    }

    private fun setupRecyclerView(
        ingredients: List<Ingredient>,
        selectedIngredientsIds: List<String>
    ) {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.ingredientRecyclerView)
        Log.d(
            "IngredientsListFragment",
            "Setting up RecyclerView with ${ingredients.size} ingredients."
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        ingredientsAdapter = IngredientListAdapter(ingredients, selectedIngredientsIds)
        recyclerView.adapter = ingredientsAdapter
    }

    private fun createShoppingListFile(
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

    // Intent para compartir el archivo de la lista de compras y poder enviarlo por correo, WhatsApp, Notas, etc.
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