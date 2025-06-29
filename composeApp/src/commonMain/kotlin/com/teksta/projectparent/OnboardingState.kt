package com.teksta.projectparent

object OnboardingState {
    private var prefs: OnboardingPrefs? = null

    fun init(prefs: OnboardingPrefs) {
        this.prefs = prefs
    }

    var isOnboardingComplete: Boolean
        get() = prefs?.getOnboardingComplete() ?: false
        set(value) { prefs?.setOnboardingComplete(value) }

    var isBabyProfileComplete: Boolean = false
    var isCardSelectionComplete: Boolean = false

    fun reset() {
        isOnboardingComplete = false
        isBabyProfileComplete = false
        isCardSelectionComplete = false
    }
} 