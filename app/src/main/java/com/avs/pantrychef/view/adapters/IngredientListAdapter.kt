package com.avs.pantrychef.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.model.Ingredient

class IngredientListAdapter(private val ingredients: List<Ingredient>, private val selectedIngredientsIds: List<String>) :
    RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder>() {

    var selectedIngredients: MutableList<Ingredient> = mutableListOf()

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientName: TextView = view.findViewById(R.id.ingredientName)
        val ingredientCheckbox: CheckBox = view.findViewById(R.id.ingredientCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.ingredientName.text = ingredient.name
        holder.ingredientCheckbox.isChecked = selectedIngredients.contains(ingredient) || selectedIngredientsIds.contains(ingredient.id) // Marcar si está en la lista previa

        holder.ingredientCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // Añade o elimina el ingrediente de la lista de seleccionados basado en el estado del CheckBox
            if (isChecked) {
                selectedIngredients.add(ingredient)
            } else {
                selectedIngredients.remove(ingredient)
            }
        }
    }

    override fun getItemCount(): Int = ingredients.size

    fun getSelectedIngredientsList(): List<Ingredient> = selectedIngredients
}
