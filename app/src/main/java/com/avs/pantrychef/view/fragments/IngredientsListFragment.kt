package com.avs.pantrychef.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.RecipeController
import com.avs.pantrychef.model.Ingredient
import com.avs.pantrychef.view.adapters.IngredientAdapter
import com.avs.pantrychef.view.adapters.IngredientListAdapter
import java.util.Locale

class IngredientsListFragment: Fragment() {

    private val args: IngredientsListFragmentArgs by navArgs()
    private lateinit var ingredientsAdapter: IngredientListAdapter
    private val recipeController = RecipeController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ingredients_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languageCode = Locale.getDefault().language
        val recipeId = args.receiptId
        val ingredientsIds = args.ingredientsIds.toList()

        recipeController.getRecipeWithIngredientsById(recipeId, languageCode, { recipe, ingredients ->
            Log.d("IngredientsList", "Ingredientes: $ingredients")
            activity?.runOnUiThread {
                // Actualizar la UI con los detalles de la receta
                view.findViewById<TextView>(R.id.tvRecipeName).text = recipe.title
                view.findViewById<TextView>(R.id.tvRecipeTime).text = "Tiempo aprox: ${recipe.preparationTime} min"
                view.findViewById<TextView>(R.id.tvRecipeDifficulty).text = "Dificultad: ${recipe.difficulty}"

                // Configurar el RecyclerView con los ingredientes
                setupRecyclerView(ingredients, ingredientsIds)
            }
        }, { exception ->
            Log.e("IngredientsListFragment", "Error fetching recipe details", exception)
        })

        view.findViewById<Button>(R.id.btnMakeShoppingList).setOnClickListener {
            // TODO: Implementar lógica para hacer la lista de compra
        }
    }

    private fun setupRecyclerView(ingredients: List<Ingredient>, selectedIngredientsIds: List<String>){
        val recyclerView: RecyclerView = requireView().findViewById(R.id.ingredientRecyclerView)
        Log.d("IngredientsListFragment", "Setting up RecyclerView with ${ingredients.size} ingredients.")
        recyclerView.layoutManager = LinearLayoutManager(context)
        ingredientsAdapter = IngredientListAdapter(ingredients, selectedIngredientsIds)
        recyclerView.adapter = ingredientsAdapter
    }
}
