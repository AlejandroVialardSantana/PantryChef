package com.avs.pantrychef.view.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.avs.pantrychef.services.TimerService
import java.util.Locale

class RecipeStepFragment: Fragment() {

    private val args: RecipeStepFragmentArgs by navArgs()
    private val recipeStepController = RecipeStepController()
    private var currentStepIndex = 0
    private var steps = listOf<Step>()
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_recipe_step, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextToSpeech()

        val recipeId = args.recipeId
        currentStepIndex = args.stepIndex
        val languageCode = Locale.getDefault().language
        Log.d("RecipeStepFragment", "Recipe ID: $recipeId, Step Index: $currentStepIndex")

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

        view.findViewById<ImageView>(R.id.mic).setOnClickListener() {
            readCurrentStep()
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter().apply {
            addAction("timer_update")
            addAction("timer_finished")
        }
        context?.registerReceiver(timerUpdateReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(timerUpdateReceiver)
    }

    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "timer_update" -> {
                    val millisUntilFinished = intent.getLongExtra("countdown", 0)
                    val minutes = millisUntilFinished / 1000 / 60
                    val seconds = millisUntilFinished / 1000 % 60
                    view?.findViewById<TextView>(R.id.btnSetTimer)?.text = String.format("%02d:%02d", minutes, seconds)
                    Log.d("RecipeStepFragment", "Timer update: $minutes:$seconds")
                }
                "timer_finished" -> {
                    view?.findViewById<TextView>(R.id.btnSetTimer)?.text = "Poner cronómetro"
                    view?.findViewById<Button>(R.id.btnSetTimer)?.isEnabled = true
                }
            }
        }
    }

    private fun setupTimerButton(timeInMinutes: Int) {
        val btnSetTimer: Button = requireView().findViewById(R.id.btnSetTimer)
        btnSetTimer.setOnClickListener {
            Log.d("RecipeStepFragment", "Setting timer for $timeInMinutes minutes")
            val intent = Intent(context, TimerService::class.java)
            intent.putExtra("timeInMinutes", timeInMinutes)
            intent.putExtra("recipeId", args.recipeId)
            intent.putExtra("stepIndex", currentStepIndex)
            context?.startService(intent)
        }
    }

    private fun displayStep(index: Int) {
        val step = steps[index]
        val stepTitle = getString(R.string.stepTitle, step.order)
        view?.findViewById<TextView>(R.id.stepTitle)?.text = stepTitle
        view?.findViewById<TextView>(R.id.stepDescription)?.text = step.description

        // Si el paso tiene tiempo, configurar y mostrar el botón del cronómetro
        if (step.time != null) {
            setupTimerButton(step.time)
            view?.findViewById<Button>(R.id.btnSetTimer)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<Button>(R.id.btnSetTimer)?.visibility = View.INVISIBLE
        }
    }

    private fun setupTextToSpeech() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("RecipeStepFragment", "This Language is not supported")
                }
            } else {
                Log.e("RecipeStepFragment", "Initialization Failed!")
            }
        }
    }

    private fun readCurrentStep() {
        steps.getOrNull(currentStepIndex)?.let { step ->
            textToSpeech.speak(step.description, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

}