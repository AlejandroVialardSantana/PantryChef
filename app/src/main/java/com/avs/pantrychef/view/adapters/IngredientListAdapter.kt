package com.avs.pantrychef.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.model.Ingredient
import com.avs.pantrychef.model.IngredientWithQuantity

/**
 * Adapter para la lista de ingredientes dentro de las recetas o de la lista de la compra.
 *
 * @param ingredients Lista de ingredientes a mostrar.
 * @param selectedIngredientsIds Lista de IDs de ingredientes seleccionados.
 */
class IngredientListAdapter(
    private val ingredientsWithQuantity:List<IngredientWithQuantity>,
    selectedIngredientsIds: List<String>
) : RecyclerView.Adapter<IngredientListAdapter.IngredientViewHolder>() {

    private val _selectedIngredientsIds: MutableList<String> = selectedIngredientsIds.toMutableList()
    val selectedIngredientsIds: List<String> get() = _selectedIngredientsIds

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientName: TextView = view.findViewById(R.id.ingredientName)
        val ingredientCheckbox: CheckBox = view.findViewById(R.id.ingredientCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_list_item, parent, false)
        return IngredientViewHolder(view)
    }

    /**
     * Actualiza la lista de ingredientes seleccionados.
     * Maneja también si ya hay algún ingrediente seleccionado.
     */
    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredientWithQuantity = ingredientsWithQuantity[position]
        val ingredient = ingredientWithQuantity.ingredient
        val quantity = ingredientWithQuantity.quantity

        val displayText = "${ingredient.name} - $quantity ${ingredient.measurementUnit}"
        holder.ingredientName.text = displayText

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
    }

    /**
     * Devuelve la lista de ingredientes con sus cantidades.
     */
    fun getIngredientsWithQuantity(): List<IngredientWithQuantity> {
        return ingredientsWithQuantity
    }


    /**
     * Devuelve la cantidad de ingredientes en la lista.
     */
    override fun getItemCount(): Int = ingredientsWithQuantity.size
}
