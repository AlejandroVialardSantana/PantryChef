package com.avs.pantrychef.views.fragments

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
import com.avs.pantrychef.controllers.RecipeStepController
import com.avs.pantrychef.models.Step
import com.avs.pantrychef.services.TimerService
import java.util.Locale

/**
 * Fragmento para mostrar los pasos de una receta
 * con la posibilidad de configurar un cronómetro
 */
class RecipeStepFragment: Fragment() {

    private val args: RecipeStepFragmentArgs by navArgs()
    private val recipeStepController = RecipeStepController()
    private var currentStepIndex = 0
    private var steps = listOf<Step>()
    private var isTimerRunning = false
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_recipe_step, container, false)
    }

    /**
     * Configura los botones de navegación y cronómetro
     * y muestra el paso actual
     */
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

    /**
     * Inicia el servicio del cronómetro
     * y registra el receptor de actualizaciones
     */
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter().apply {
            addAction("timer_update")
            addAction("timer_finished")
        }
        context?.registerReceiver(timerUpdateReceiver, filter)
    }

    /**
     * Detiene el servicio del cronómetro
     * y elimina el receptor de actualizaciones
     */
    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(timerUpdateReceiver)
    }

    /**
     * Receptor de actualizaciones del cronómetro
     */
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
                    isTimerRunning = false
                    updateStepNavigationButtons()
                }
            }
        }
    }

    /**
     * Configura el botón del cronómetro
     * y lo inicia con el tiempo especificado
     *
     * @param timeInMinutes tiempo en minutos
     */
    private fun setupTimerButton(timeInMinutes: Int) {
        val btnSetTimer: Button = requireView().findViewById(R.id.btnSetTimer)
        btnSetTimer.setOnClickListener {
            Log.d("RecipeStepFragment", "Setting timer for $timeInMinutes minutes")
            isTimerRunning = true
            updateStepNavigationButtons()
            val intent = Intent(context, TimerService::class.java)
            intent.putExtra("timeInMinutes", timeInMinutes)
            intent.putExtra("recipeId", args.recipeId)
            intent.putExtra("stepIndex", currentStepIndex)
            context?.startService(intent)
        }
    }

    /**
     * Muestra el paso en la vista
     *
     * @param index índice del paso
     */
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

        updateStepNavigationButtons()
    }

    /**
     * Configura el TextToSpeech para leer los pasos
     */
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

    /**
     * Lee el paso actual en voz alta con TextToSpeech
     */
    private fun readCurrentStep() {
        steps.getOrNull(currentStepIndex)?.let { step ->
            textToSpeech.speak(step.description, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    /**
     * Actualiza la visibilidad de los botones de navegación en base a si el cronómetro está corriendo
     */
    private fun updateStepNavigationButtons() {
        val nextStepButton: ImageView = requireView().findViewById(R.id.nextStep)
        val previousStepButton: ImageView = requireView().findViewById(R.id.previousStep)

        val enableButtons = !isTimerRunning
        nextStepButton.isEnabled = enableButtons && currentStepIndex < steps.size - 1
        previousStepButton.isEnabled = enableButtons && currentStepIndex > 0
    }

    /**
     * Detiene el TextToSpeech al destruir el fragmento
     */
    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }

}