package com.teksta.projectparent.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

/**
 * Android's actual implementation for the BottleFeedTrackerManager.
 * This class is responsible for starting and stopping the BottleTrackingService.
 * The service itself handles the timer, notification updates, and broadcasts.
 */
actual class BottleFeedTrackerManager actual constructor(contextAsAny: Any) {
    private val context: Context = contextAsAny as Context

    companion object {
        private const val TAG = "BottleFeedTrackerManager"
        // The fully qualified name of your service class, which now lives in the androidApp module.
        private const val BOTTLE_TRACKING_SERVICE_FQN = "com.teksta.projectparent.android.services.BottleTrackingService"
        private const val ACTION_STOP_TRACKING = "com.teksta.projectparent.ACTION_STOP_TRACKING"
    }

    /**
     * Starts the BottleTrackingService in the foreground.
     */
    actual fun startTracking(babyName: String, estimatedEndTimeStamp: Long, startTimeStamp: Long) {
        Log.d(TAG, "Android: Requesting to start tracking for $babyName")
        val serviceIntent = Intent().apply {
            setClassName(context, BOTTLE_TRACKING_SERVICE_FQN)
            putExtra("babyName", babyName)
            putExtra("estimatedEndTimeStamp", estimatedEndTimeStamp)
            putExtra("startTimeStamp", startTimeStamp)
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    /**
     * Sends a command to stop the BottleTrackingService.
     */
    actual fun stopTracking() {
        Log.d(TAG, "Android: Requesting to stop tracking.")
        val serviceIntent = Intent().apply {
            setClassName(context, BOTTLE_TRACKING_SERVICE_FQN)
            // Set a custom action that the service can listen for to stop itself.
            action = ACTION_STOP_TRACKING
        }
        // Use startService for a command, as the service is already running.
        // This will deliver the new intent to onStartCommand.
        context.startService(serviceIntent)
    }
}