package com.avs.pantrychef.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.avs.pantrychef.R
import com.avs.pantrychef.view.activities.HomeActivity
import java.util.Locale

class TimerService: Service() {

    private var countDownTimer: CountDownTimer? = null
    private var recipeId: String? = null
    private var stepIndex: Int = -1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timeInMinutes = intent?.getIntExtra("timeInMinutes", 0) ?: 0
        recipeId = intent?.getStringExtra("recipeId")
        stepIndex = intent?.getIntExtra("stepIndex", -1) ?: -1
        if (timeInMinutes > 0) {
            startTimer(timeInMinutes)
        }
        return START_NOT_STICKY
    }

    /**
     * Crea un intent que abre la aplicación cuando el usuario toca la notificación
     *
     * @return Intent
     */
    private fun createNotificationIntent(): Intent {
        // Crea el intent con los extras necesarios
        return Intent(this, HomeActivity::class.java).apply {
            putExtra("notificationRecipeId", recipeId)
            putExtra("notificationStepIndex", stepIndex)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
    }

    /**
     * Inicia un temporizador con la duración especificada en minutos
     *
     * @param timeInMinutes Int
     */
    private fun startTimer(timeInMinutes: Int) {
        countDownTimer?.cancel() // Cancelar cualquier timer existente
        countDownTimer = object : CountDownTimer(timeInMinutes * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                val notification = getUpdatedNotification(getString(R.string.timerTimeLeft, formattedTime))
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(NOTIFICATION_ID, notification)

                val intent = Intent("timer_update")
                intent.putExtra("countdown", millisUntilFinished)
                sendBroadcast(intent)
            }

            override fun onFinish() {
                // Notifica al usuario que el tiempo ha terminado
                val intent = Intent("timer_finished")
                sendBroadcast(intent)
                notifyUserTimeIsUp()
            }
        }.start()

        // Iniciar el servicio en primer plano con una notificación inicial
        val initialNotification = getInitialNotification()
        startForeground(NOTIFICATION_ID, initialNotification)
    }

    /**
     * Crea una notificación actualizada con el texto especificado
     *
     * @param contentText String
     * @return Notification
     */
    private fun getUpdatedNotification(contentText: String): Notification {
        // Asegurarse de que el canal de notificación esté creado
        createNotificationChannel()

        // Configura un intento pendiente que abra la aplicación cuando el usuario toque la notificación
        val intent = createNotificationIntent()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Construye y devuelve la notificación actualizada
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.timerTitle))
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    /**
     * Crea una notificación inicial para mostrar al usuario cuando el temporizador se inicia
     *
     * @return Notification
     */
    private fun getInitialNotification(): Notification {
        // Asegurarse de que el canal de notificación esté creado
        createNotificationChannel()

        // Configura un intento pendiente que abra la aplicación cuando el usuario toque la notificación
        val intent = createNotificationIntent()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Construye y devuelve la notificación
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("PantryChef Timer")
            .setContentText(getString(R.string.timerStarted))
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    /**
     * Método que notifica al usuario que el tiempo ha terminado
     */
    private fun notifyUserTimeIsUp() {
        createNotificationChannel()

        val intent = createNotificationIntent()

        Log.d("TimerService", "Recipe ID: $recipeId, Step Index: $stepIndex")

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle("PantryChef")
            .setContentText("¡Tu tiempo de cocción ha terminado!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "timer_channel"
    }

    /**
     * Crea un canal de notificación para dispositivos con Android Oreo y superior
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channelName)
            val descriptionText = getString(R.string.channelDescription)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Detiene el temporizador y elimina la notificación cuando el servicio se destruye
     */
    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
        countDownTimer?.cancel()
    }
}