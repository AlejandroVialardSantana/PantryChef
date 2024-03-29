package com.avs.pantrychef.services

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
import com.avs.pantrychef.view.activities.MainActivity

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

    private fun startTimer(timeInMinutes: Int) {
        countDownTimer?.cancel() // Cancelar cualquier timer existente
        countDownTimer = object : CountDownTimer(timeInMinutes * 60 * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
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
    }

    private fun notifyUserTimeIsUp() {
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notificationRecipeId", recipeId)
            putExtra("notificationStepIndex", stepIndex)
        }

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

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}