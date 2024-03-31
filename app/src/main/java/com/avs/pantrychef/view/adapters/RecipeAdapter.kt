package com.avs.pantrychef.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.model.Recipe

class RecipeAdapter(private val recipes: MutableList<Recipe>,
                    private val onFavoriteSelected: (Recipe) -> Unit) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    private var onRecipeSelected: ((Recipe) -> Unit)? = null

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeName: TextView = view.findViewById(R.id.recipeName)
        val recipeTime: TextView = view.findViewById(R.id.recipeTime)
        val recipeDifficulty: TextView = view.findViewById(R.id.recipeDifficulty)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.recipeName.text = recipe.title
        holder.recipeTime.text = "Tiempo aprox: ${recipe.preparationTime} min"
        holder.recipeDifficulty.text = "Dificultad: ${recipe.difficulty}"

        holder.itemView.setOnClickListener {
            onRecipeSelected?.invoke(recipe)
        }

        holder.favoriteIcon.setImageResource(
           R.drawable.ic_fav_filled
        )

        holder.favoriteIcon.setOnClickListener() {
            onFavoriteSelected(recipe)
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun removeRecipe(recipe: Recipe) {
        val index = recipes.indexOfFirst { it.id == recipe.id }
        if (index != -1) {
            recipes.removeAt(index)
            notifyItemRemoved(index)
        }
    }

}