package com.teksta.projectparent

import com.russhwolf.settings.Settings

object OnboardingState {
    private val settings: Settings = Settings()

    var isOnboardingComplete: Boolean
        get() = settings.getBoolean("onboarding_complete", false)
        set(value) { settings.putBoolean("onboarding_complete", value) }

    var isBabyProfileComplete: Boolean
        get() = settings.getBoolean("baby_profile_complete", false)
        set(value) { settings.putBoolean("baby_profile_complete", value) }

    var isCardSelectionComplete: Boolean
        get() = settings.getBoolean("card_selection_complete", false)
        set(value) { settings.putBoolean("card_selection_complete", value) }

    fun reset() {
        isOnboardingComplete = false
        isBabyProfileComplete = false
        isCardSelectionComplete = false
    }
} 