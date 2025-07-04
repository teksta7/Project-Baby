package com.teksta.projectparent

import android.os.Build
import android.content.Context
import com.teksta.projectparent.BottleFeedForegroundService
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import android.content.BroadcastReceiver
import android.provider.Settings

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun startBottleFeedForegroundService(babyName: String, elapsed: Int, total: Int, average: Int) {
    val context = MainActivity.instance?.applicationContext ?: return
    BottleFeedForegroundService.startService(context, babyName, elapsed, total, average)
}

actual fun updateBottleFeedForegroundService(elapsed: Int, total: Int, average: Int) {
    val context = MainActivity.instance?.applicationContext ?: return
    BottleFeedForegroundService.updateService(context, elapsed, total, average)
}

actual fun stopBottleFeedForegroundService() {
    val context = MainActivity.instance?.applicationContext ?: return
    BottleFeedForegroundService.stopService(context)
}

actual fun scheduleBottleFeedNotification(delaySeconds: Long, title: String, body: String) {
    val context = MainActivity.instance?.applicationContext ?: return
    val intent = Intent(context, BottleFeedNotificationReceiver::class.java).apply {
        putExtra("title", title)
        putExtra("body", body)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        1001, // requestCode
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerAtMillis = SystemClock.elapsedRealtime() + delaySeconds * 1000
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        triggerAtMillis,
        pendingIntent
    )
}

actual fun requestScheduleExactAlarmPermissionIfNeeded(context: Any, onResult: (Boolean) -> Unit) {
    val ctx = context as? Context ?: return onResult(false)
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
        onResult(true)
        return
    }
    val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (alarmManager.canScheduleExactAlarms()) {
        onResult(true)
        return
    }
    // Prompt user to allow exact alarms in system settings
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    try {
        ctx.startActivity(intent)
    } catch (e: Exception) {
        onResult(false)
        return
    }
    // We can't know the result immediately, so assume false for now
    onResult(false)
}

// BroadcastReceiver to show the notification
class BottleFeedNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Bottle Reminder"
        val body = intent.getStringExtra("body") ?: "It's time for the next bottle."
        val channelId = "bottle_feed_reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Bottle Feed Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1001, notification)
    }
}