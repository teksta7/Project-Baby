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
import androidx.core.content.ContextCompat // <<< ADD THIS IMPORT
import com.teksta.projectparent.android.R as AppR

actual class BottleFeedTrackerManager actual constructor(contextAsAny: Any) {
    // This needs a Context. Adjust your expect class or instantiation.
    private val context: Context = contextAsAny as Context // Cast to Android Context
    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        private const val TAG = "BottleFeedTracker"
        // Define the FQN of your service here for clarity and reusability
        private const val BOTTLE_TRACKING_SERVICE_FQN = "com.teksta.projectparent.android.services.BottleTrackingService"
    }

    actual fun startTracking(babyName: String, estimatedEndTimeStamp: Long, startTimeStamp: Long) {
        Log.d(TAG, "Android: Start Tracking for $babyName")

        // Create an intent that explicitly targets the service in the androidApp module
        val serviceIntent = Intent()
        serviceIntent.setClassName(context, BOTTLE_TRACKING_SERVICE_FQN) // Use FQN
        // Or if context is from androidApp:
        // val serviceIntent = Intent(context, com.teksta.projectparent.android.services.BottleTrackingService::class.java)

        serviceIntent.apply {
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
        try {
            val notification = NotificationCompat.Builder(context, "BottleFeedTrackingChannel")
                .setContentTitle("Bottle Feed In Progress") // You might need babyName here
                .setContentText("Duration: $durationText")
                .setSmallIcon(com.teksta.projectparent.android.R.drawable.ic_notification) // Replace with your app icon TO SORT
                .setOngoing(true)
                // .addAction(R.drawable.ic_stop, "Stop", getStopPendingIntent()) // Re-add actions
                .build()
            notificationManager.notify(101, notification) // Use const for NOTIFICATION_ID
        }
        catch (e: Exception) {
            Log.e("BottleFeedTracker", "Error updating notification in background: ${e.message}", e)
            // This exception could be the cause of your crash.
        }
    }

    actual fun stopTracking() {
        Log.d(TAG, "Android: Stop Tracking")
        val serviceIntent = Intent()
        serviceIntent.setClassName(context, BOTTLE_TRACKING_SERVICE_FQN) // Use FQN string
        context.stopService(serviceIntent)
        notificationManager.cancel(101) // Use const for NOTIFICATION_ID
    }

    // Helper to create pending intent, if actions are built here
    private fun getStopPendingIntent(): PendingIntent {
        val stopIntent = Intent()
        stopIntent.setClassName(context, BOTTLE_TRACKING_SERVICE_FQN) // Use FQN string
        stopIntent.action = "com.teksta.projectparent.ACTION_STOP_TRACKING" // Use const for ACTION_STOP_TRACKING

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getService(context, 0, stopIntent, flags)
    }
}