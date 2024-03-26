package com.avs.pantrychef.view.fragments

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.RecipeController
import com.avs.pantrychef.model.Recipe
import java.util.Locale

class RecipeListFragment : Fragment() {

    private val args: RecipeListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout para este fragmento
        return inflater.inflate(R.layout.fragment_receipt_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        // Crear una vista de tarjeta para la receta
        val cardView = layoutInflater.inflate(R.layout.card_recipe, container, false)

        // Encuentra los componentes de la vista
        val recipeName: TextView = cardView.findViewById(R.id.recipeName)
        val recipeTime: TextView = cardView.findViewById(R.id.recipeTime)
        val recipeDifficulty: TextView = cardView.findViewById(R.id.recipeDifficulty)

        // Configurar los valores de los componentes de la vista
        recipeName.text = recipe.title
        recipeTime.text = "Tiempo aprox: ${recipe.preparationTime} min"
        recipeDifficulty.text = "Dificultad: ${recipe.difficulty}"

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
    }
}