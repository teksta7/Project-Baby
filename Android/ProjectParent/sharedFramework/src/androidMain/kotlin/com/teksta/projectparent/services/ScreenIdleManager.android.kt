package com.teksta.projectparent.services

import android.app.Activity
import android.view.WindowManager

actual class ScreenIdleManager actual constructor(activityAsAny: Any) {
    // The constructor now takes an Activity.
    // You'll need to ensure an Activity instance is passed when creating this manager.
    // In your ViewModel, you might need a way to initialize this with the current Activity,
    // or pass the activity reference from your UI layer (MainActivity).
    private val activity = activityAsAny as? Activity


    actual fun keepScreenOn() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    actual fun allowScreenToDim() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}