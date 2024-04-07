package com.avs.pantrychef.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.controllers.RecipeController
import com.avs.pantrychef.controllers.UserController
import com.avs.pantrychef.models.Recipe
import com.avs.pantrychef.views.adapters.RecipeAdapter
import java.util.Locale

class FavsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private var userController = UserController()
    private var recipeController = RecipeController()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout para este fragmento
        return inflater.inflate(R.layout.fragment_favs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languageCode = Locale.getDefault().language

        recyclerView = view.findViewById(R.id.favsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadFavoriteRecipes(languageCode)
    }

    /**
     * Cargar las recetas favoritas del usuario
     *
     * @param languageCode Código de idioma para las recetas
     */
    private fun loadFavoriteRecipes(languageCode: String) {

        userController.getFavoriteRecipes(onSuccess = { favoriteRecipes ->
            recipeController.fetchRecipesByIds(favoriteRecipes, languageCode, onSuccess = { recipes ->
                activity?.runOnUiThread {
                    setupRecyclerView(recipes)
                }
            }, onFailure = {
                Log.e("FavsFragment", "Error fetching recipes by ids", it)
            })
        }, onFailure = {
            Log.e("FavsFragment", "Error fetching favorite recipes")
        })
    }

    /**
     * Configurar el RecyclerView con las recetas favoritas.
     * Permitir eliminar recetas de la lista de favoritos.
     *
     * @param recipes Lista de recetas favoritas
     */
    private fun setupRecyclerView(recipes: List<Recipe>) {
        recipeAdapter = RecipeAdapter(
            recipes.toMutableList(),
            onFavoriteSelected = { recipe ->
                userController.removeRecipeFromFavorites(recipe.id, onSuccess = {
                    activity?.runOnUiThread {
                        recipeAdapter.removeRecipe(recipe)
                    }
                }, onFailure = {
                    Log.e("FavsFragment", "Error removing favorite recipe")
                })
            },
            onRecipeClicked = { recipeId ->
                // Navegar a IngredientsListFragment aquí
                val action = FavsFragmentDirections.actionFavsFragmentToIngredientsListFragment(recipeId, emptyArray())
                findNavController().navigate(action)
            }
        )
        recyclerView.adapter = recipeAdapter
    }

}