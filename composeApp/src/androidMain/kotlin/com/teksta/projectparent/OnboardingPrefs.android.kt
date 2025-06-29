package com.teksta.projectparent

import android.content.Context
import android.content.SharedPreferences

actual class OnboardingPrefs actual constructor(context: Any) {
    private val context = context as Context
    private val prefs: SharedPreferences
        get() = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)

    actual fun getOnboardingComplete(): Boolean =
        prefs.getBoolean("onboarding_complete", false)

    actual fun setOnboardingComplete(value: Boolean) {
        prefs.edit().putBoolean("onboarding_complete", value).apply()
    }
} 