package com.avs.pantrychef.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.RecipeStepController
import com.avs.pantrychef.model.Step
import java.util.Locale

class RecipeStepFragment: Fragment() {

    private val args: RecipeStepFragmentArgs by navArgs()
    private val recipeStepController = RecipeStepController()
    private var currentStepIndex = 0
    private var steps = listOf<Step>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_recipe_step, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipeId = args.recipeId
        val languageCode = Locale.getDefault().language

        recipeStepController.fetchStepsByRecipeId(
            recipeId,
            languageCode,
            { stepsFetched ->
                steps = stepsFetched
                displayStep(currentStepIndex)
            },
            { exception ->
                Log.e("RecipeStepFragment", "Error fetching steps", exception)
            }
        )

        view.findViewById<ImageView>(R.id.nextStep).setOnClickListener {
            if (currentStepIndex < steps.size - 1) {
                currentStepIndex++
                displayStep(currentStepIndex)
            }
        }

        view.findViewById<ImageView>(R.id.previousStep).setOnClickListener {
            if (currentStepIndex > 0) {
                currentStepIndex--
                displayStep(currentStepIndex)
            }
        }
    }

    private fun displayStep(index: Int) {
        val step = steps[index]
        view?.findViewById<TextView>(R.id.stepTitle)?.text = "Paso ${step.order}:"
        view?.findViewById<TextView>(R.id.stepDescription)?.text = step.description

        // Si el paso tiene tiempo, mostrar el botón del cronómetro
        view?.findViewById<Button>(R.id.btnSetTimer)?.visibility = if (step.time != null) View.VISIBLE else View.INVISIBLE
    }

}