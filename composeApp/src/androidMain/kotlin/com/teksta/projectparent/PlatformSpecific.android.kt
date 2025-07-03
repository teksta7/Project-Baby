package com.teksta.projectparent

import android.util.Log

actual fun logDebug(tag: String, message: String) {
    Log.d(tag, message)
}
