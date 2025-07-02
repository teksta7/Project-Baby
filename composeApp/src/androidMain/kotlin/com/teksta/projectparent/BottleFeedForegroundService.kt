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

class BottleFeedForegroundService : Service() {
    companion object {
        const val CHANNEL_ID = "bottle_feed_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "ACTION_START_BOTTLE_FEED"
        const val ACTION_UPDATE = "ACTION_UPDATE_BOTTLE_FEED"
        const val ACTION_STOP = "ACTION_STOP_BOTTLE_FEED"
        const val EXTRA_ELAPSED = "EXTRA_ELAPSED"
        const val EXTRA_TOTAL = "EXTRA_TOTAL"
        const val EXTRA_BABY_NAME = "EXTRA_BABY_NAME"

        fun startService(context: Context, babyName: String, elapsed: Int, total: Int) {
            val intent = Intent(context, BottleFeedForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_BABY_NAME, babyName)
                putExtra(EXTRA_ELAPSED, elapsed)
                putExtra(EXTRA_TOTAL, total)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun updateService(context: Context, elapsed: Int, total: Int) {
            val intent = Intent(context, BottleFeedForegroundService::class.java).apply {
                action = ACTION_UPDATE
                putExtra(EXTRA_ELAPSED, elapsed)
                putExtra(EXTRA_TOTAL, total)
            }
            context.startService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, BottleFeedForegroundService::class.java)
            intent.action = ACTION_STOP
            context.startService(intent)
        }
    }

    private var babyName: String = ""
    private var elapsed: Int = 0
    private var total: Int = 0

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                babyName = intent.getStringExtra(EXTRA_BABY_NAME) ?: "Baby"
                elapsed = intent.getIntExtra(EXTRA_ELAPSED, 0)
                total = intent.getIntExtra(EXTRA_TOTAL, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    startForeground(NOTIFICATION_ID, buildNotification(), 8)
                } else {
                    startForeground(NOTIFICATION_ID, buildNotification())
                }
            }
            ACTION_UPDATE -> {
                elapsed = intent.getIntExtra(EXTRA_ELAPSED, 0)
                total = intent.getIntExtra(EXTRA_TOTAL, 0)
                updateNotification()
            }
            ACTION_STOP -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val percent = if (total > 0) (elapsed * 100 / total).coerceIn(0, 100) else 0
        val contentText = "${babyName}'s bottle feed: ${formatTime(elapsed)} / ${formatTime(total)}"
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Bottle Feed in Progress")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setProgress(100, percent, false)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification() {
        val notification = buildNotification()
        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
    }

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

    private fun formatTime(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", min, sec)
    }
} 