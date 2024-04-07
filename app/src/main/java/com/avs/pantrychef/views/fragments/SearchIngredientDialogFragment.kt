package com.avs.pantrychef.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.avs.pantrychef.controllers.IngredientController
import com.avs.pantrychef.databinding.DialogSearchIngredientBinding
import com.avs.pantrychef.views.adapters.IngredientDialogAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.util.Locale

/**
 * Fragmento que muestra un diálogo con la lista de ingredientes para seleccionar
 * y buscar recetas con esos ingredientes.
 */
class SearchIngredientDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogSearchIngredientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogSearchIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchIngredients()
    }

    /**
     * Obtiene la lista de ingredientes desde la API y configura el RecyclerView.
     * También configura el botón de búsqueda de recetas.
     */
    private fun fetchIngredients() {
        val languageCode = Locale.getDefault().language

        IngredientController().fetchIngredients(languageCode, onSuccess = { ingredients ->
            val adapter = IngredientDialogAdapter(ingredients).apply {
                onIngredientSelectionChanged = {
                    updateSelectedIngredients(this)
                }
            }
            binding.ingredientRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.ingredientRecyclerView.adapter = adapter

            binding.searchRecipesButton.setOnClickListener {
                val selectedIngredientsIds = adapter.getSelectedIngredientsList().map { it.id }.toTypedArray()

                val action = SearchIngredientDialogFragmentDirections.actionSearchIngredientDialogFragmentToReceiptListFragment(selectedIngredientsIds)
                findNavController().navigate(action)
            }

            binding.searchEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {
                    if (adapter.filteredIngredients.isEmpty()) {
                        binding.textViewNoResults.visibility = View.VISIBLE
                    } else {
                        binding.textViewNoResults.visibility = View.GONE
                    }
                }
            })
        }, onFailure = { exception ->
            Log.e("SearchIngredientDialog", "Error fetching ingredients", exception)
        })
    }

    /**
     * Limpia la referencia al binding para evitar memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Actualiza la lista de ingredientes seleccionados en la vista.
     * Los muestra con un Chip en el ChipGroup.
     *
     * @param adapter Adaptador del RecyclerView
     */
    private fun updateSelectedIngredients(adapter: IngredientDialogAdapter) {
        binding.chipGroupSelectedIngredients.removeAllViews()
        adapter.getSelectedIngredientsList().forEach { ingredient ->
            val chip = Chip(context).apply {
                text = ingredient.name
                isCloseIconVisible = true
                setOnCloseIconClickListener {
                    adapter.toggleIngredientSelection(ingredient)
                }
            }
            binding.chipGroupSelectedIngredients.addView(chip)
        }
    }
}
