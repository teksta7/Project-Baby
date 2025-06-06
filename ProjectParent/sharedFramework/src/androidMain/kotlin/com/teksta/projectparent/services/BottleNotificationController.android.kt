package com.teksta.projectparent.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

actual class BottleNotificationController actual constructor(contextHolderAsAny: Any) {
    private val context: Context = contextHolderAsAny as Context
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    companion object {
        const val REMINDER_CHANNEL_ID = "BottleReminderChannel"
        const val REQUEST_CODE_PREFIX = 1000 // To help create unique request codes
        // Define the FQN of your receiver here for clarity and reusability
        private const val NOTIFICATION_RECEIVER_FQN = "com.teksta.projectparent.android.services.NotificationReceiver"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Bottle Reminders"
            val descriptionText = "Reminders for when the next bottle feed is due"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(REMINDER_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    actual suspend fun requestNotificationAccessByUser() {
        // As noted before, the actual permission prompt should be triggered from the UI (Activity).
        // This function can check the status or prepare the request.
        Log.d("BottleNotification", "Checking notification permissions...")
    }

    actual suspend fun scheduleBottleNotification(minutesUntilNotification: Int, bottleID: String) {
        val triggerAtMillis = System.currentTimeMillis() + (minutesUntilNotification * 60 * 1000)

        // --- CORRECTED INTENT CREATION ---
        val intent = Intent()
        intent.setClassName(context, NOTIFICATION_RECEIVER_FQN) // Use the FQN string
        intent.apply {
            // Your existing extras are still needed
            putExtra("babyName", "Your baby")
            putExtra("notification_id", REQUEST_CODE_PREFIX + bottleID.hashCode())
            putExtra("bottle_id", bottleID)
        }
        // --- END OF CORRECTION ---

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_PREFIX + bottleID.hashCode(), // Unique request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.w("BottleNotification", "Cannot schedule exact alarms. Notification may be delayed.")
                // Fallback to a less exact alarm or guide user to settings
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                Log.d("BottleNotification", "Scheduled notification for bottle $bottleID at $triggerAtMillis")
            }
        } catch (e: SecurityException) {
            Log.e("BottleNotification", "Missing SCHEDULE_EXACT_ALARM or USE_EXACT_ALARM permission?", e)
        }
    }

    actual fun removeExistingNotifications(notificationIdentifier: String) {
        // Cancel a scheduled alarm
        // --- CORRECTED INTENT CREATION ---
        val intent = Intent()
        intent.setClassName(context, NOTIFICATION_RECEIVER_FQN)
        // --- END OF CORRECTION ---
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_PREFIX + notificationIdentifier.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        // Cancel a visible notification
        notificationManager.cancel(REQUEST_CODE_PREFIX + notificationIdentifier.hashCode())
        Log.d("BottleNotification", "Removed scheduled/visible notification for ID: $notificationIdentifier")
    }

    actual fun removeAllNotifications() {
        // This is a more aggressive cancellation. Be careful using it.
        // It doesn't cancel future alarms, only removes visible notifications.
        notificationManager.cancelAll()
        Log.d("BottleNotification", "Removed all visible notifications for this app.")
    }
}