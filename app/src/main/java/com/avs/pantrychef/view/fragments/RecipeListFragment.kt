package com.avs.pantrychef.view.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.RecipeController
import com.avs.pantrychef.controller.UserController
import com.avs.pantrychef.model.Recipe
import java.util.Locale

class RecipeListFragment : Fragment() {

    private val args: RecipeListFragmentArgs by navArgs()
    private val userController = UserController()
    private val favoriteRecipes = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout para este fragmento
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadFavoriteRecipes()

        val ingredientIds = args.ingredientIds

        val container: LinearLayout = view.findViewById(R.id.recipesContainer)

        val recipeController = RecipeController()

        val languageCode = Locale.getDefault().language

        // Llamar al método para obtener recetas basadas en ingredientes seleccionados
        recipeController.fetchRecipesByIngredients(ingredientIds, languageCode, onSuccess = { recipes ->
            // Ejecutar en el hilo principal de la interfaz de usuario
            activity?.runOnUiThread {
                container.removeAllViews() // Limpiar las recetas anteriores
                recipes.forEach { recipe ->
                    addRecipeCardToView(recipe, ingredientIds, container)
                }
            }
        }, onFailure = { exception ->
            Log.e("ReceiptListFragment", "Error fetching recipes", exception)
        })
    }

    private fun addRecipeCardToView(recipe: Recipe, ingredientIds: Array<String>, container: LinearLayout) {
        // Crear una vista de tarjeta para la recetas
        val cardView = layoutInflater.inflate(R.layout.card_recipe, container, false)

        // Encuentra los componentes de la vista
        val recipeName: TextView = cardView.findViewById(R.id.recipeName)
        val recipeTime: TextView = cardView.findViewById(R.id.recipeTime)
        val recipeDifficulty: TextView = cardView.findViewById(R.id.recipeDifficulty)
        val favoriteIcon: ImageView = cardView.findViewById(R.id.favoriteIcon)

        val isFavorite = recipe.id in favoriteRecipes
        if (isFavorite) {
            favoriteIcon.setImageResource(R.drawable.ic_fav_filled)
        } else {
            favoriteIcon.setImageResource(R.drawable.ic_favs)
        }

        // Configurar los valores de los componentes de la vista
        recipeName.text = recipe.title
        recipeTime.text = getString(R.string.timeAprox, recipe.preparationTime)
        recipeDifficulty.text = getString(R.string.difficulty, recipe.difficulty)

        // Configurar los márgenes para la vista
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            val marginInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
            ).toInt()
            setMargins(0, marginInPixels, 0, marginInPixels)
        }
        cardView.layoutParams = layoutParams

        cardView.setOnClickListener {
            // Navegar a la pantalla de detalles de la receta
            val action = RecipeListFragmentDirections.actionReceiptListFragmentToIngredientsListFragment(recipe.id, ingredientIds)
            view?.findNavController()?.navigate(action)
        }

        container.addView(cardView)

        favoriteIcon.setOnClickListener { view ->
            val isFavorite = recipe.id in favoriteRecipes
            if (isFavorite) {
                userController.removeRecipeFromFavorites(recipe.id,
                    onSuccess = {
                        favoriteIcon.setImageResource(R.drawable.ic_favs) // Ícono para no favorito
                        favoriteRecipes.remove(recipe.id)
                    },
                    onFailure = { error ->
                        Log.e("RecipeListFragment", "Error removing recipe from favorites: $error")
                    })
            } else {
                userController.addRecipeToFavorites(recipe.id,
                    onSuccess = {
                        favoriteIcon.setImageResource(R.drawable.ic_fav_filled) // Ícono para favorito
                        favoriteRecipes.add(recipe.id)
                    },
                    onFailure = { error ->
                        Log.e("RecipeListFragment", "Error adding recipe to favorites: $error")
                    })
            }
        }
    }

    private fun loadFavoriteRecipes() {
        userController.getFavoriteRecipes(onSuccess = { recipes ->
            favoriteRecipes.clear()
            favoriteRecipes.addAll(recipes)
        }, onFailure = {
            Log.e("RecipeListFragment", "Error loading favorite recipes: $it")
        })
    }
}