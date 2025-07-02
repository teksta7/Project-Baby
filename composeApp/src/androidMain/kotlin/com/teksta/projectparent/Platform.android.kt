package com.teksta.projectparent

import android.os.Build
import android.content.Context
import com.teksta.projectparent.BottleFeedForegroundService

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun startBottleFeedForegroundService(babyName: String, elapsed: Int, total: Int) {
    val context = MainActivity.instance?.applicationContext ?: return
    BottleFeedForegroundService.startService(context, babyName, elapsed, total)
}

actual fun updateBottleFeedForegroundService(elapsed: Int, total: Int) {
    val context = MainActivity.instance?.applicationContext ?: return
    BottleFeedForegroundService.updateService(context, elapsed, total)
}

actual fun stopBottleFeedForegroundService() {
    val context = MainActivity.instance?.applicationContext ?: return
    BottleFeedForegroundService.stopService(context)
}