package com.teksta.projectparent

expect class OnboardingPrefs(context: Any) {
    fun getOnboardingComplete(): Boolean
    fun setOnboardingComplete(value: Boolean)
} 