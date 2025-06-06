package com.teksta.projectparent.android.services // Or your actual package

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.teksta.projectparent.android.R // Ensure this R is from your androidApp

class BottleTrackingService : Service() {
    companion object {
        const val CHANNEL_ID = "BottleFeedTrackingChannel"
        const val NOTIFICATION_ID = 101
        const val ACTION_STOP_TRACKING = "com.teksta.projectparent.ACTION_STOP_TRACKING"
    }

    override fun onCreate() {
        super.onCreate()
        // Create the Notification Channel once when the service is first created.
        // This is the correct place for channel creation.
        createNotificationChannel()
        Log.d("BottleTrackingService", "onCreate: Service created and channel creation initiated.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BottleTrackingService", "onStartCommand: Service commanded to start.")
        if (intent?.action == ACTION_STOP_TRACKING) {
            stopForeground(true) // true = remove notification
            stopSelf()
            Log.d("BottleTrackingService", "onStartCommand: ACTION_STOP_TRACKING received, service stopping.")
            return START_NOT_STICKY
        }

        val babyName = intent?.getStringExtra("babyName") ?: "Baby"
        // val startTimeStamp = intent?.getLongExtra("startTimeStamp", System.currentTimeMillis()) ?: System.currentTimeMillis() // You might use this in notification

        // Build the initial notification required for startForeground()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("$babyName's Bottle Feed")
            .setContentText("Tracking started... (00:00)")
            .setSmallIcon(com.teksta.projectparent.android.R.drawable.ic_notification) // Ensure this icon exists and is valid!
            .setOngoing(true) // Makes it persistent
            // You might want to add a PendingIntent to open the app when notification is tapped
            // .setContentIntent(yourPendingIntentToOpenApp)
            .build()

        // --- THIS IS THE CRITICAL CALL AND ITS CORRECT PLACEMENT ---
        try {
            startForeground(NOTIFICATION_ID, notification)
            Log.d("BottleTrackingService", "startForeground called successfully.")
        } catch (e: Exception) {
            Log.e("BottleTrackingService", "Error calling startForeground: ${e.message}", e)
            // If startForeground fails, the service will be killed by the system shortly.
            // This often happens if the notification is invalid (e.g., no small icon).
            stopSelf() // Stop the service if it can't become a foreground service.
            return START_NOT_STICKY
        }

        // Return START_STICKY if you want the service to be restarted if the system kills it.
        // Return START_NOT_STICKY if you don't want it to restart automatically.
        // Return START_REDELIVER_INTENT if you want it to restart with the last Intent.
        return START_STICKY
    }

    private fun createNotificationChannel() {
        // ... (your existing channel creation code)
        // Ensure this is robust and logs success/failure.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bottle Feed Tracking"
            val descriptionText = "Shows ongoing bottle feed progress"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("BottleTrackingService","Notification channel $CHANNEL_ID created.")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d("BottleTrackingService", "onDestroy: Service being destroyed.")
        super.onDestroy()
    }
}