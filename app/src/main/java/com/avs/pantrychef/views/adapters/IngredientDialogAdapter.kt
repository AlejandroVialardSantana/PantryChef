package com.avs.pantrychef.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avs.pantrychef.R
import com.avs.pantrychef.models.Ingredient

/**
 * Adaptador para el RecyclerView que muestra los ingredientes en un diálogo
 * para seleccionar ingredientes al presionar el botón de añadir ingredientes
 * en la pantalla de home.
 *
 * @property ingredients Lista de ingredientes a mostrar.
 */
class IngredientDialogAdapter(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientDialogAdapter.IngredientViewHolder>(), Filterable {

    var filteredIngredients: List<Ingredient> = emptyList()
    var selectedIngredients: MutableList<Ingredient> = mutableListOf()
    var onIngredientSelectionChanged: (() -> Unit)? = null

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientName: TextView = view.findViewById(R.id.ingredientName)
        val ingredientCheckbox: CheckBox = view.findViewById(R.id.ingredientCheckbox)
    }

    /**
     * Función que se llama al crear un nuevo ViewHolder.
     * Crea y devuelve un nuevo ViewHolder que contiene la vista del ingrediente con checkbox.
     *
     * @param parent Grupo de vistas al que se añadirá el nuevo ViewHolder.
     * @param viewType Tipo de vista del nuevo ViewHolder.
     * @return Nuevo ViewHolder creado.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ingredient_item, parent, false)
        return IngredientViewHolder(view)
    }

    /**
     * Función que se llama al enlazar un ViewHolder con un elemento de la lista.
     * Muestra el nombre del ingrediente y marca el checkbox si está seleccionado.
     *
     * @param holder ViewHolder que se va a enlazar.
     * @param position Posición del elemento en la lista.
     */
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

    /**
     * Función que devuelve el número de elementos en la lista de ingredientes.
     *
     * @return Número de elementos en la lista.
     */
    override fun getItemCount(): Int = filteredIngredients.size

    /**
     * Función que devuelve los ingredientes seleccionados.
     *
     * @return Lista de ingredientes seleccionados.
     */
    fun getSelectedIngredientsList(): List<Ingredient> {
        return selectedIngredients
    }

    /**
     * Función que devuelve un filtro para filtrar los ingredientes por nombre.
     * Se encarga de filtrar los ingredientes según el texto introducido en el campo de búsqueda,
     * si contiene el texto introducido en el nombre del ingrediente y si no sale vacío.
     *
     * @return Filtro para filtrar los ingredientes.
     */
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

    /**
     * Función que se encarga de añadir o quitar un ingrediente de la lista de seleccionados.
     * Si el ingrediente ya está en la lista de seleccionados, lo quita.
     * Si no está en la lista, lo añade.
     *
     * @param ingredient Ingrediente que se va a añadir/quitar de la lista de seleccionados.
     */
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
