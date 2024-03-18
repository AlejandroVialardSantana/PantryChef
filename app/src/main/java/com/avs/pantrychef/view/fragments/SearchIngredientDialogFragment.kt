package com.avs.pantrychef.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.avs.pantrychef.databinding.DialogSearchIngredientBinding
import com.avs.pantrychef.model.Ingredient
import com.avs.pantrychef.view.adapters.IngredientAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class SearchIngredientDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogSearchIngredientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogSearchIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí configuras tu RecyclerView y el buscador
        // Configura el adaptador del RecyclerView con un listado hardcodeado de ingredientes
        val ingredients = listOf(
            Ingredient("1", "Pimiento rojo", "unidades"),
            Ingredient("2", "Pimiento verde", "unidades"),
            Ingredient("3", "Cebolla", "unidades"),
            Ingredient("4", "Tomate", "unidades"),
            Ingredient("5", "Ajo", "unidades"),
            Ingredient("6", "Aceite de oliva", "cucharadas"),
            Ingredient("7", "Sal", "cucharaditas"),
            Ingredient("8", "Pimiento morrón", "cucharaditas"),
            // Agrega más ingredientes
        )

        Log.d("SearchIngredientDialogFragment", "Ingredientes: $ingredients")

        val adapter = IngredientAdapter(ingredients).apply {
            onIngredientSelectionChanged = {
                updateSelectedIngredients(this)
            }
        }

        binding.ingredientRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.ingredientRecyclerView.adapter = adapter

        binding.searchRecipesButton.setOnClickListener {
            Toast.makeText(requireContext(), "Se presionó el botón de buscar recetas", Toast.LENGTH_SHORT).show()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateSelectedIngredients(adapter: IngredientAdapter) {
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
