package com.teksta.projectparent.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
//import com.teksta.projectparent.R
import androidx.core.content.ContextCompat // <<< ADD THIS IMPORT
import com.teksta.projectparent.android.R


// --- Define your Service Class (typically in its own file in androidApp) ---
// This is a very basic example of what your service might look like.
class BottleTrackingService : Service() {
    companion object {
        const val CHANNEL_ID = "BottleFeedTrackingChannel"
        const val NOTIFICATION_ID = 101
        const val ACTION_STOP_TRACKING = "com.teksta.projectparent.ACTION_STOP_TRACKING"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_TRACKING) {
            stopForeground(true)
            stopSelf()
            // Potentially notify BottleFeedTrackerManager to update its state
            return START_NOT_STICKY
        }

        val babyName = intent?.getStringExtra("babyName") ?: "Baby"
        val startTimeStamp = intent?.getLongExtra("startTimeStamp", System.currentTimeMillis()) ?: System.currentTimeMillis()

        // Initial notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("$babyName's Bottle Feed")
            .setContentText("Tracking started... (00:00)")
            .setSmallIcon(R.drawable.ic_notification_bottle) // Replace with your app icon TO SORT
            .setOngoing(true) // Makes it persistent
            // .addAction(R.drawable.ic_stop, "Stop", getStopPendingIntent()) // Example stop action
            .build()

        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY // Or START_NOT_STICKY depending on desired behavior
    }

    private fun getStopPendingIntent(): PendingIntent {
        val stopIntent = Intent(this, BottleTrackingService::class.java).apply {
            action = ACTION_STOP_TRACKING
        }
        return PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bottle Feed Tracking"
            val descriptionText = "Shows ongoing bottle feed progress"
            val importance = NotificationManager.IMPORTANCE_LOW // Low to avoid sound, but still visible
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}


actual class BottleFeedTrackerManager actual constructor(contextAsAny: Any) {
    // This needs a Context. Adjust your expect class or instantiation.
    private val context: Context = contextAsAny as Context // Cast to Android Context
    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        private const val TAG = "BottleFeedTracker"
    }

    actual fun startTracking(babyName: String, estimatedEndTimeStamp: Long, startTimeStamp: Long) {
        Log.d(TAG, "Android: Start Tracking for $babyName")
        val serviceIntent = Intent(context, BottleTrackingService::class.java).apply {
            putExtra("babyName", babyName)
            putExtra("estimatedEndTimeStamp", estimatedEndTimeStamp)
            putExtra("startTimeStamp", startTimeStamp)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    actual fun updateTracking(currentDurationSeconds: Int) {
        Log.d(TAG, "Android: Update Tracking - Duration: $currentDurationSeconds s")
        // To update, the service itself would typically handle building and re-issuing the notification
        // Or this manager could re-build and notify if it has all the necessary original info.
        // For simplicity, this example assumes the service updates its own notification.
        // Alternatively, you could send an Intent to the running service to trigger an update.

        // Example of updating notification directly from here (if service isn't managing it):
        val minutes = currentDurationSeconds / 60
        val seconds = currentDurationSeconds % 60
        val durationText = String.format("%02d:%02d", minutes, seconds)

        // Retrieve babyName or have it stored if needed for re-building
        val notification = NotificationCompat.Builder(context, BottleTrackingService.CHANNEL_ID)
            .setContentTitle("Bottle Feed In Progress") // You might need babyName here
            .setContentText("Duration: $durationText")
            //.setSmallIcon(com.teksta.projectparent.android.R.drawable.ic_notification_bottle) // Replace with your app icon TO SORT
            .setOngoing(true)
            // .addAction(R.drawable.ic_stop, "Stop", getStopPendingIntent()) // Re-add actions
            .build()
        notificationManager.notify(BottleTrackingService.NOTIFICATION_ID, notification)
    }

    actual fun stopTracking() {
        Log.d(TAG, "Android: Stop Tracking")
        val serviceIntent = Intent(context, BottleTrackingService::class.java)
        context.stopService(serviceIntent)
        notificationManager.cancel(BottleTrackingService.NOTIFICATION_ID)
    }

    // Helper to create pending intent, if actions are built here
    private fun getStopPendingIntent(): PendingIntent {
        val stopIntent = Intent(context, BottleTrackingService::class.java).apply {
            action = BottleTrackingService.ACTION_STOP_TRACKING
        }
        return PendingIntent.getService(
            context, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}