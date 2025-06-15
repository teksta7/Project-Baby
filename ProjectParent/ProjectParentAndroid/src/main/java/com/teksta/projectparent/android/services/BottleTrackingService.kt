package com.teksta.projectparent.android.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.teksta.projectparent.android.MainActivity
import com.teksta.projectparent.android.R
import com.teksta.projectparent.navigation.AppRoutes
import java.util.Timer
import kotlin.concurrent.timerTask

class BottleTrackingService : Service() {

    companion object {
        const val ACTION_TIMER_UPDATE = "com.teksta.projectparent.TIMER_UPDATE"
        const val EXTRA_DURATION_SECONDS = "duration_seconds"
        const val CHANNEL_ID = "BottleFeedTrackingChannel"
        const val NOTIFICATION_ID = 101
        const val ACTION_STOP_TRACKING = "com.teksta.projectparent.ACTION_STOP_TRACKING"
    }

    private var timer: Timer? = null
    private var durationSeconds = 0
    private var startTimeMillis = 0L
    private lateinit var babyName: String

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d("BottleTrackingService", "onCreate: Service created and channel creation initiated.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BottleTrackingService", "onStartCommand received.")

        if (intent?.action == ACTION_STOP_TRACKING) {
            stopTimer()
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        babyName = intent?.getStringExtra("babyName") ?: "Baby"
        startTimeMillis = intent?.getLongExtra("startTimeStamp", System.currentTimeMillis()) ?: System.currentTimeMillis()
        durationSeconds = ((System.currentTimeMillis() - startTimeMillis) / 1000).toInt()

        // Build the initial notification required for startForeground()
        val notification = buildNotification(durationSeconds)

        try {
            startForeground(NOTIFICATION_ID, notification)
            Log.d("BottleTrackingService", "startForeground called successfully.")
        } catch (e: Exception) {
            Log.e("BottleTrackingService", "Error calling startForeground: ${e.message}", e)
            stopSelf() // Stop the service if it can't become a foreground service.
            return START_NOT_STICKY
        }

        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        stopTimer()
        Log.d("BottleTrackingService", "onDestroy: Service being destroyed.")
        super.onDestroy()
    }

    private fun startTimer() {
        if (timer != null) return // Ensure only one timer runs
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            durationSeconds++

            // Action 1: Update the foreground notification
            val updatedNotification = buildNotification(durationSeconds)
            val notificationManager = NotificationManagerCompat.from(this@BottleTrackingService)
            try {
                notificationManager.notify(NOTIFICATION_ID, updatedNotification)
            } catch (e: SecurityException) {
                // This can happen if notification permission is revoked while service is running
                Log.e("BottleTrackingService", "Permission error updating notification. Stopping timer.", e)
                stopTimer()
            }

            // Action 2: Send a broadcast for the UI to update if it's visible
            val intent = Intent(ACTION_TIMER_UPDATE)
            intent.putExtra(EXTRA_DURATION_SECONDS, durationSeconds)
            sendBroadcast(intent)
        }, 1000, 1000)
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    /**
     * Helper function to consistently build the notification for both initial start and updates.
     */
    private fun buildNotification(currentDuration: Int): Notification {
        val minutes = currentDuration / 60
        val seconds = currentDuration % 60
        val durationText = String.format("%02d:%02d", minutes, seconds)

        val contentTitle = if (currentDuration > 0) "Bottle Feed In Progress" else "$babyName's Bottle Feed"

        val pendingIntent = createActivityPendingIntent()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setContentText("Duration: $durationText")
            .setSmallIcon(com.teksta.projectparent.android.R.drawable.ic_notification) // Your valid notification icon
            .setOngoing(true) // Makes the notification persistent
            .setContentIntent(pendingIntent) // Makes the notification clickable
            .build()
    }

    /**
     * Creates the PendingIntent that opens the app directly to the BottlesScreen using a deep link.
     */
    private fun createActivityPendingIntent(): PendingIntent {
        val notificationIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(AppRoutes.BOTTLES_DEEPLINK) // Use the deep link URI
        )

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(this, 0, notificationIntent, flags)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bottle Feed Tracking"
            val descriptionText = "Shows ongoing bottle feed progress"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}