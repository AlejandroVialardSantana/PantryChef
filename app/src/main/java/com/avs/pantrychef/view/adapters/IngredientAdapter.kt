package com.avs.pantrychef.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.model.Ingredient

class IngredientAdapter(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>(), Filterable {

    var filteredIngredients: List<Ingredient> = emptyList()
    var selectedIngredients: MutableList<Ingredient> = mutableListOf()
    var onIngredientSelectionChanged: (() -> Unit)? = null

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientName: TextView = view.findViewById(R.id.ingredientName)
        val ingredientCheckbox: CheckBox = view.findViewById(R.id.ingredientCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = filteredIngredients[position]
        holder.ingredientName.text = ingredient.name

        // Marcar el checkbox si el ingrediente está seleccionado
        holder.ingredientCheckbox.isChecked = selectedIngredients.contains(ingredient)

        // Manejar clics en el item para agregar/quitar de la lista de seleccionados
        holder.itemView.setOnClickListener {
            toggleIngredientSelection(ingredient)
        }

        // Manejar clics en el checkbox para agregar/quitar de la lista de seleccionados
        holder.ingredientCheckbox.setOnClickListener {
            toggleIngredientSelection(ingredient)
        }
    }

    override fun getItemCount(): Int = filteredIngredients.size

    fun getSelectedIngredientsList(): List<Ingredient> {
        return selectedIngredients
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val search = constraint.toString()
                if (search.isEmpty()) {
                    filteredIngredients = emptyList()
                } else {
                    filteredIngredients = ingredients.filter { it.name.lowercase().contains(search.lowercase()) }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredIngredients
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredIngredients = results?.values as List<Ingredient>
                notifyDataSetChanged()
            }
        }
    }

    fun toggleIngredientSelection(ingredient: Ingredient) {
        if (selectedIngredients.contains(ingredient)) {
            selectedIngredients.remove(ingredient)
        } else {
            selectedIngredients.add(ingredient)
        }
        notifyDataSetChanged()
        onIngredientSelectionChanged?.invoke()
    }
}
