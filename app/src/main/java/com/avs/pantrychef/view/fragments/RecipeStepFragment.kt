package com.avs.pantrychef.view.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
    private var countDownTimer: CountDownTimer? = null

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

    private fun setupTimerButton(timeInSeconds: Int) {
        val btnSetTimer: Button = requireView().findViewById(R.id.btnSetTimer)
        btnSetTimer.setOnClickListener {
            startTimer(timeInSeconds)
        }
    }

    private fun startTimer(timeInMinutes: Int) {
        countDownTimer?.cancel() // Cancelar cualquier timer existente
        countDownTimer = object : CountDownTimer(timeInMinutes * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Actualiza el texto del botón con el tiempo restante
                val minutesLeft = millisUntilFinished / 1000 / 60
                val secondsLeft = (millisUntilFinished / 1000) % 60
                val btnSetTimer: Button = requireView().findViewById(R.id.btnSetTimer)
                btnSetTimer.text = String.format("Tiempo restante: %d:%02d", minutesLeft, secondsLeft)
            }

            override fun onFinish() {
                // Notifica al usuario que el tiempo ha terminado
                // checkNotificationPermissionAndShow()
                notifyUserTimeIsUp()
            }
        }.start()
    }

    private fun notifyUserTimeIsUp() {
        createNotificationChannel()
        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("PantryChef")
            .setContentText("¡Tu tiempo de cocción ha terminado!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "timer_channel"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channelName)
            val descriptionText = getString(R.string.channelDescription)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun displayStep(index: Int) {
        val step = steps[index]
        view?.findViewById<TextView>(R.id.stepTitle)?.text = "Paso ${step.order}:"
        view?.findViewById<TextView>(R.id.stepDescription)?.text = step.description

        // Si el paso tiene tiempo, configurar y mostrar el botón del cronómetro
        if (step.time != null) {
            setupTimerButton(step.time)
            view?.findViewById<Button>(R.id.btnSetTimer)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<Button>(R.id.btnSetTimer)?.visibility = View.INVISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        countDownTimer?.cancel()
    }
}