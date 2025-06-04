package com.teksta.projectparent.services

import android.content.Context

// This would typically be initialized in your Application class
// and provided through a DI framework or a singleton.
// For simplicity, this example requires it to be set.
object AndroidAppContext {
    lateinit var applicationContext: Context
}

actual fun getPlatformContext(): Any = AndroidAppContext.applicationContext