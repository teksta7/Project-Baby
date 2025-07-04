// BottleFeedForegroundService manages the persistent notification and background logic for bottle feed tracking on Android.
package com.teksta.projectparent

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.teksta.projectparent.R
import com.teksta.projectparent.db.AppDatabaseWrapper
import com.teksta.projectparent.db.BottleFeedRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BottleFeedForegroundService : Service() {
    companion object {
        // Notification and action constants
        const val CHANNEL_ID = "bottle_feed_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START_BOTTLE_FEED"
        const val ACTION_UPDATE = "ACTION_UPDATE_BOTTLE_FEED"
        const val ACTION_STOP = "ACTION_STOP_BOTTLE_FEED"
        const val ACTION_CANCEL = "ACTION_CANCEL_BOTTLE_FEED"
        const val ACTION_FINISH = "ACTION_FINISH_BOTTLE_FEED"
        const val EXTRA_ELAPSED = "EXTRA_ELAPSED"
        const val EXTRA_TOTAL = "EXTRA_TOTAL"
        const val EXTRA_BABY_NAME = "EXTRA_BABY_NAME"
        const val EXTRA_AVERAGE = "EXTRA_AVERAGE"

        // Start the foreground service for a new bottle feed
        fun startService(context: Context, babyName: String, elapsed: Int, total: Int, average: Int) {
            val intent = Intent(context, BottleFeedForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_BABY_NAME, babyName)
                putExtra(EXTRA_ELAPSED, elapsed)
                putExtra(EXTRA_TOTAL, total)
                putExtra(EXTRA_AVERAGE, average)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        // Update the notification with new progress
        fun updateService(context: Context, elapsed: Int, total: Int, average: Int) {
            val intent = Intent(context, BottleFeedForegroundService::class.java).apply {
                action = ACTION_UPDATE
                putExtra(EXTRA_ELAPSED, elapsed)
                putExtra(EXTRA_TOTAL, total)
                putExtra(EXTRA_AVERAGE, average)
            }
            context.startService(intent)
        }

        // Stop the foreground service
        fun stopService(context: Context) {
            val intent = Intent(context, BottleFeedForegroundService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }

        // Dismiss all notifications related to bottle feeds
        fun dismissAllNotifications(context: Context) {
            NotificationManagerCompat.from(context).cancelAll()
        }
    }

    // State for the current feed
    private var babyName: String = ""
    private var elapsed: Int = 0
    private var total: Int = 0
    private var average: Int = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    // Handle service commands (start, update, cancel, finish, stop)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                android.util.Log.d("BottleFeedService", "ACTION_START received")
                babyName = intent.getStringExtra(EXTRA_BABY_NAME) ?: "Baby"
                elapsed = intent.getIntExtra(EXTRA_ELAPSED, 0)
                total = intent.getIntExtra(EXTRA_TOTAL, 0)
                average = intent.getIntExtra(EXTRA_AVERAGE, 0)
                // Start the persistent notification
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(NOTIFICATION_ID, buildNotification(), 1)
                } else {
                    startForeground(NOTIFICATION_ID, buildNotification())
                }
            }
            ACTION_UPDATE -> {
                android.util.Log.d("BottleFeedService", "ACTION_UPDATE received")
                elapsed = intent.getIntExtra(EXTRA_ELAPSED, 0)
                total = intent.getIntExtra(EXTRA_TOTAL, 0)
                average = intent.getIntExtra(EXTRA_AVERAGE, 0)
                updateNotification()
            }
            ACTION_CANCEL -> {
                android.util.Log.d("BottleFeedService", "ACTION_CANCEL received")
                clearInProgressState(sendBroadcast = true)
                stopForeground(true)
                stopSelf()
            }
            ACTION_FINISH -> {
                android.util.Log.d("BottleFeedService", "ACTION_FINISH received")
                saveFeedAndFinish()
                stopForeground(true)
                stopSelf()
            }
            ACTION_STOP -> {
                android.util.Log.d("BottleFeedService", "ACTION_STOP received")
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Build the notification UI for the foreground service
    private fun buildNotification(): Notification {
        val progressMax = if (average > 0) average else total
        val percent = if (progressMax > 0) (elapsed * 100 / progressMax).coerceIn(0, 100) else 0
        val contentText = "${babyName}'s bottle feed: ${formatTime(elapsed)} / ${formatTime(progressMax)}"

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bottle Feed in Progress")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setProgress(100, percent, false)
            .setOngoing(true)
            .build()
    }

    // Update the notification with new progress
    private fun updateNotification() {
        val notification = buildNotification()
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

    // Create the notification channel for Android O+
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Bottle Feed Progress",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Shows bottle feed progress in real time."
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    // Format seconds as mm:ss for notification display
    private fun formatTime(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", min, sec)
    }

    // Clear the in-progress state and optionally broadcast cancellation
    private fun clearInProgressState(sendBroadcast: Boolean = false) {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        android.util.Log.d("BottleFeedService", "clearInProgressState called, sendBroadcast=$sendBroadcast")
        prefs.edit()
            .putBoolean("bottle_in_progress", false)
            .remove("bottle_start_time")
            .remove("bottle_duration")
            .remove("bottle_ounces")
            .remove("bottle_notes")
            .putBoolean("bottle_feed_finished_externally", true)
            .putString("bottle_feed_action", if (sendBroadcast) "cancel" else "")
            .apply()
        NotificationManagerCompat.from(this).cancelAll()
        if (sendBroadcast) {
            val intent = Intent("com.teksta.projectparent.BOTTLE_FEED_CANCELLED")
            android.util.Log.d("BottleFeedService", "Sending BOTTLE_FEED_CANCELLED broadcast")
            sendBroadcast(intent)
        }
        stopSelf()
    }

    // Save the completed feed to the database and finish the service
    private fun saveFeedAndFinish() {
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        android.util.Log.d("BottleFeedService", "saveFeedAndFinish called")
        val startTime = prefs.getLong("bottle_start_time", 0L)
        val duration = prefs.getInt("bottle_duration", 0)
        val ounces = prefs.getString("bottle_ounces", "0.0")?.toDoubleOrNull() ?: 0.0
        val notes = prefs.getString("bottle_notes", "") ?: ""
        if (startTime > 0 && duration > 0 && ounces > 0.0) {
            GlobalScope.launch {
                val db = AppDatabaseWrapper(applicationContext)
                val repo = BottleFeedRepository(db)
                val endTime = startTime + duration
                android.util.Log.d("BottleFeedService", "Inserting feed into database: startTime=$startTime, endTime=$endTime, duration=$duration, ounces=$ounces, notes=$notes")
                repo.insertFeed(
                    startTime = startTime,
                    endTime = endTime,
                    duration = duration.toDouble(),
                    ounces = ounces,
                    additionalNotes = notes
                )
            }
        }
        clearInProgressState(sendBroadcast = true)
    }
} 