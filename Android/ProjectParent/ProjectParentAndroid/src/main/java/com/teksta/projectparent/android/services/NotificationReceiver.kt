package com.teksta.projectparent.android.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID_KEY = "notification_id"
        const val BOTTLE_ID_KEY = "bottle_id"
        const val CHANNEL_ID = "BottleReminderChannel" // A different channel for reminders
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "Alarm received!")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val babyName = intent.getStringExtra("babyName") ?: "Your baby"
        val notificationId = intent.getIntExtra(NOTIFICATION_ID_KEY, 0)

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(com.teksta.projectparent.android.R.drawable.ic_notification) // Your notification icon
            .setContentTitle("$babyName's next bottle is due")
            .setContentText("Remember to track the next bottle feed in the app.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(notificationId, notification)
            Log.d("NotificationReceiver", "Notification posted with ID: $notificationId")
        } catch (e: SecurityException) {
            Log.e("NotificationReceiver", "Missing POST_NOTIFICATIONS permission?", e)
        }
    }
}