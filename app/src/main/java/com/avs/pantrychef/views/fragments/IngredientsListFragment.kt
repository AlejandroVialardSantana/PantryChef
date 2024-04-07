package com.avs.pantrychef.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.controllers.RecipeController
import com.avs.pantrychef.helpers.ShoppingListHelper
import com.avs.pantrychef.models.IngredientWithQuantity
import com.avs.pantrychef.views.adapters.IngredientListAdapter
import java.util.Locale

/**
 * Fragmento para mostrar la lista de ingredientes de una receta y los datos de esta.
 * Permite al usuario marcar los ingredientes que ya tiene y generar una lista de compras.
 */
class IngredientsListFragment: Fragment() {

    private val args: IngredientsListFragmentArgs by navArgs()
    private lateinit var ingredientsAdapter: IngredientListAdapter
    private val recipeController = RecipeController()
    private val shoppingListHelper = ShoppingListHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredients_list, container, false)
    }

    /**
     * Carga los datos de la receta y los ingredientes, y configura la UI.
     * Además, configura los botones para generar la lista de compras y comenzar la receta.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languageCode = Locale.getDefault().language
        val recipeId = args.recipeId
        val ingredientsIds = args.ingredientsIds.toList()

        recipeController.getRecipeWithIngredientsById(
            recipeId,
            languageCode,
            { recipe, ingredientsWithQuantity ->
                Log.d("IngredientsList", "Ingredientes: $ingredientsWithQuantity")
                activity?.runOnUiThread {
                    // Actualizar la UI con los detalles de la receta
                    view.findViewById<TextView>(R.id.tvRecipeName).text = recipe.title
                    view.findViewById<TextView>(R.id.tvRecipeTime).text =
                        getString(R.string.timeAprox, recipe.preparationTime)
                    view.findViewById<TextView>(R.id.tvRecipeDifficulty).text =
                        getString(R.string.difficulty, recipe.difficulty)

                    // Configurar el RecyclerView con los ingredientes
                    setupRecyclerView(ingredientsWithQuantity, ingredientsIds)
                }
            },
            { exception ->
                Log.e("IngredientsListFragment", "Error fetching recipe details", exception)
            })

        view.findViewById<Button>(R.id.btnMakeShoppingList).setOnClickListener {
            val ingredientsWithQuantity = ingredientsAdapter.getIngredientsWithQuantity()
            val selectedIngredientsIds = ingredientsAdapter.selectedIngredientsIds

            val fileUri = shoppingListHelper.createShoppingListFile(
                ingredientsWithQuantity,
                selectedIngredientsIds,
                requireContext()
            )
            if (fileUri != null) {
                shoppingListHelper.shareShoppingListFile(fileUri, requireContext())
            }
        }

        view.findViewById<Button>(R.id.btnStartCooking).setOnClickListener {
            val action = IngredientsListFragmentDirections.actionIngredientsListFragmentToRecipeStepFragment(recipeId, 0)
            view?.findNavController()?.navigate(action)
        }
    }

    private fun setupRecyclerView(
        ingredientsWithQuantity: List<IngredientWithQuantity>,
        selectedIngredientsIds: List<String>
    ) {
        val recyclerView: RecyclerView = requireView().findViewById(R.id.ingredientRecyclerView)
        Log.d(
            "IngredientsListFragment",
            "Setting up RecyclerView with ${ingredientsWithQuantity.size} ingredients."
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        ingredientsAdapter = IngredientListAdapter(ingredientsWithQuantity, selectedIngredientsIds)
        recyclerView.adapter = ingredientsAdapter
    }
}