package com.avs.pantrychef.view.fragments

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.avs.pantrychef.R

class ReceiptListFragment : Fragment() {

    private val args: ReceiptListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout para este fragmento
        return inflater.inflate(R.layout.fragment_receipt_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí puedes acceder a los argumentos
        val ingredientIds = args.ingredientIds.joinToString(separator = ", ")

        // Encuentra el TextView por su ID y asigna los IDs de los ingredientes como su texto
        // val ingredientIdsTextView = view.findViewById<TextView>(R.id.receiptsText)
        // ingredientIdsTextView.text = ingredientIds

        val container: LinearLayout = view.findViewById(R.id.recipesContainer)

        // TODO: Obtener recetas de la base de datos y mostrarlas en la vista
        for (i in 1..5) {
            val cardView = layoutInflater.inflate(R.layout.card_recipe, container, false)
            val recipeName: TextView = cardView.findViewById(R.id.recipeName)
            val recipeTime: TextView = cardView.findViewById(R.id.recipeTime)
            val recipeDifficulty: TextView = cardView.findViewById(R.id.recipeDifficulty)
            val favoriteIcon: ImageView = cardView.findViewById(R.id.favoriteIcon)

            // Configurar los márgenes para la vista
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val marginInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics
            ).toInt() // Convierte 8dp a píxeles

            layoutParams.setMargins(0, marginInPixels, 0, marginInPixels) // Aplicar márgenes
            cardView.layoutParams = layoutParams


            recipeName.text = "Receta $i"
            recipeTime.text = "Tiempo aprox: 20 min"
            recipeDifficulty.text = "Dificultad: Fácil"


            favoriteIcon.setOnClickListener {
                // TODO: Cambiar el corazon a lleno y guardar la receta en favoritos
            }

            container.addView(cardView)
        }
    }
}