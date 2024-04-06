package com.avs.pantrychef.view.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.RecipeController
import com.avs.pantrychef.controller.UserController
import com.avs.pantrychef.helpers.ShoppingListHelper
import com.avs.pantrychef.model.Ingredient
import com.avs.pantrychef.model.IngredientWithQuantity
import com.avs.pantrychef.view.adapters.IngredientListAdapter
import java.io.File
import java.io.IOException
import java.util.Locale

/**
 * Fragmento que muestra la lista de ingredientes de las recetas favoritas del usuario
 * y permite generar una lista de compras con los ingredientes seleccionados.
 */
class ShoppingListFragment : Fragment() {

    private lateinit var userController: UserController
    private lateinit var recipeController: RecipeController
    private lateinit var ingredientsAdapter: IngredientListAdapter
    private lateinit var recipeNames: MutableList<String>
    private lateinit var recipeMap: MutableMap<String, String>
    private val shoppingListHelper = ShoppingListHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout para este fragmento
        return inflater.inflate(R.layout.fragment_shopping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userController = UserController()
        recipeController = RecipeController()

        val ingredientsRecyclerView = view.findViewById<RecyclerView>(R.id.ingredientsShoppingRecyclerView)
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(context)

        ingredientsAdapter = IngredientListAdapter(emptyList(), emptyList())
        ingredientsRecyclerView.adapter = ingredientsAdapter

        loadFavoriteIngredients()

        view.findViewById<Button>(R.id.buttonMakeShoppingList).setOnClickListener {
            val selectedIngredientsWithQuantity = ingredientsAdapter.getIngredientsWithQuantity()
            val selectedIngredientsIds = ingredientsAdapter.selectedIngredientsIds

            val fileUri = shoppingListHelper.createShoppingListFile(selectedIngredientsWithQuantity, selectedIngredientsIds, requireContext())
            if (fileUri != null) {
                shoppingListHelper.shareShoppingListFile(fileUri, requireContext())
            }
        }
    }

    /**
     * Carga las recetas favoritas del usuario y sus ingredientes.
     * Configura el Spinner para seleccionar una receta y el RecyclerView para mostrar los ingredientes.
     */
    private fun loadFavoriteIngredients() {
        val languageCode = Locale.getDefault().language

        userController.getFavoriteRecipes(
            onSuccess = { favoriteRecipeIds ->
                recipeNames = mutableListOf(getString(R.string.allRecipes))
                recipeMap = mutableMapOf()

                favoriteRecipeIds.forEachIndexed { index, recipeId ->
                    recipeController.getRecipeById(
                        recipeId,
                        languageCode,
                        { recipe ->
                            recipeNames.add(recipe.title)
                            recipeMap[recipe.title] = recipe.id

                            // Cargar los ingredientes y configurar el Spinner una vez que todas las recetas hayan sido cargadas
                            if (index == favoriteRecipeIds.size - 1) {
                                setupRecipeSpinner()
                                setupRecyclerView(emptyList()) // Inicializa el RecyclerView sin ingredientes
                                loadIngredientsForAllRecipes(favoriteRecipeIds)
                            }
                        },
                        { exception ->
                            Log.e("ShoppingListFragment", "Error fetching recipe details", exception)
                        }
                    )
                }
            },
            onFailure = { exception ->
                Log.e("ShoppingListFragment", "Error fetching favorite recipes")
            }
        )
    }

    /**
     * Configura el Spinner para seleccionar una receta y cargar los ingredientes correspondientes.
     */
    private fun setupRecipeSpinner() {
        val spinner: Spinner = requireView().findViewById(R.id.spinnerFilterRecipes)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, recipeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    // Cargar ingredientes para todas las recetas
                    loadIngredientsForAllRecipes(recipeMap.values.toList())
                } else {
                    // Cargar ingredientes para la receta seleccionada
                    val selectedRecipeId = recipeMap[recipeNames[position]]
                    selectedRecipeId?.let {
                        loadIngredientsForSingleRecipe(it)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                // No hacer nada
            }
        }
    }

    /**
     * Carga los ingredientes de todas las recetas favoritas del usuario.
     *
     * @param recipeIds Lista de IDs de las recetas favoritas del usuario
     */
    private fun loadIngredientsForAllRecipes(recipeIds: List<String>) {
        val allIngredientsWithQuantity = mutableListOf<IngredientWithQuantity>()

        recipeIds.forEachIndexed { index, recipeId ->
            recipeController.getRecipeWithIngredientsById(
                recipeId,
                Locale.getDefault().language,
                { _, ingredientsWithQuantity ->
                    allIngredientsWithQuantity.addAll(ingredientsWithQuantity)
                    // Asegurarse de actualizar el RecyclerView solo después de cargar todos los ingredientes
                    if (index == recipeIds.size - 1) {
                        setupRecyclerView(allIngredientsWithQuantity)
                    }
                },
                { exception ->
                    Log.e("ShoppingListFragment", "Error loading ingredients for recipe $recipeId", exception)
                }
            )
        }
    }

    /**
     * Carga los ingredientes de una receta específica.
     *
     * @param recipeId ID de la receta
     */
    private fun loadIngredientsForSingleRecipe(recipeId: String) {
        recipeController.getRecipeWithIngredientsById(
            recipeId,
            Locale.getDefault().language,
            { _, ingredientsWithQuantity ->
                setupRecyclerView(ingredientsWithQuantity)
            },
            { exception ->
                Log.e("ShoppingListFragment", "Error loading ingredients for recipe $recipeId", exception)
            }
        )
    }

    private fun setupRecyclerView(ingredientsWithQuantity: List<IngredientWithQuantity>, selectedIngredientsIds: List<String> = emptyList()) {
        ingredientsAdapter = IngredientListAdapter(ingredientsWithQuantity, selectedIngredientsIds)
        view?.findViewById<RecyclerView>(R.id.ingredientsShoppingRecyclerView)?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsAdapter
        }
    }
}