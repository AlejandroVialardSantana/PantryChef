package com.avs.pantrychef.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.model.Ingredient

class IngredientListAdapter(
    private val ingredients: List<Ingredient>,
    selectedIngredientsIds: List<String>
) : RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder>() {

    private val _selectedIngredientsIds: MutableList<String> = selectedIngredientsIds.toMutableList()
    val selectedIngredientsIds: List<String> get() = _selectedIngredientsIds
    val ingredientsList: List<Ingredient> get() = ingredients

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientName: TextView = view.findViewById(R.id.ingredientName)
        val ingredientCheckbox: CheckBox = view.findViewById(R.id.ingredientCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_list_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]

        // Quitar temporalmente el listener para evitar triggers indeseados
        holder.ingredientCheckbox.setOnCheckedChangeListener(null)

        // Establecer estado del CheckBox basado en si el ingrediente está seleccionado
        holder.ingredientCheckbox.isChecked = selectedIngredientsIds.contains(ingredient.id)

        // Volver a establecer el listener
        holder.ingredientCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                _selectedIngredientsIds.add(ingredient.id)
            } else {
                _selectedIngredientsIds.remove(ingredient.id)
            }
        }

        holder.ingredientName.text = ingredient.name
    }

    override fun getItemCount(): Int = ingredients.size
}
