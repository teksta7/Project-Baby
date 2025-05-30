package com.teksta.projectparent.onboarding

// In sharedFramework/src/commonMain/kotlin/com/teksta/projectparent/onboarding/CardSelectionViewModel.kt

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Represents the state of each card toggle
data class CardSelectionUiState(
    val isBottlesCardTracked: Boolean = true, // Default to true as per original Swift
    val isSleepCardTracked: Boolean = false,
    val isFoodCardTracked: Boolean = false,
    val isMedsCardTracked: Boolean = false,
    val isWindCardTracked: Boolean = false,
    val isPooCardTracked: Boolean = false,
    // For Bottle Notification
    val enableBottleNotification: Boolean = false
)

// Expect declaration for platform-specific notification controller
expect class BottleNotificationController() {
    suspend fun requestNotificationAccessByUser()
}

class CardSelectionViewModel {
    private val settings: Settings = Settings()
    private val bottleNotificationController: BottleNotificationController = BottleNotificationController()

    private val _uiState = MutableStateFlow(CardSelectionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Load initial state from settings
        _uiState.update {
            it.copy(
                isBottlesCardTracked = settings.getBoolean("isBottlesCardTracked", true),
                isSleepCardTracked = settings.getBoolean("isSleepCardTracked", false),
                isFoodCardTracked = settings.getBoolean("isFoodCardTracked", false),
                isMedsCardTracked = settings.getBoolean("isMedsCardTracked", false),
                isWindCardTracked = settings.getBoolean("isWindCardTracked", false),
                isPooCardTracked = settings.getBoolean("isPooCardTracked", false),
                enableBottleNotification = settings.getBoolean("enableBottleNotification", false)
            )
        }
    }

    fun onBottlesCardToggled(isTracked: Boolean) {
        settings.putBoolean("isBottlesCardTracked", isTracked)
        _uiState.update { it.copy(isBottlesCardTracked = isTracked) }
        // Note: The direct update to `HomeCards[1].toTrack` from Swift
        // would be handled differently in KMP. Typically, the UI that displays
        // HomeCards would observe these settings directly or through a repository.
    }

    fun onSleepCardToggled(isTracked: Boolean) {
        settings.putBoolean("isSleepCardTracked", isTracked)
        _uiState.update { it.copy(isSleepCardTracked = isTracked) }
    }

    fun onFoodCardToggled(isTracked: Boolean) {
        settings.putBoolean("isFoodCardTracked", isTracked)
        _uiState.update { it.copy(isFoodCardTracked = isTracked) }
    }

    fun onMedsCardToggled(isTracked: Boolean) {
        settings.putBoolean("isMedsCardTracked", isTracked)
        _uiState.update { it.copy(isMedsCardTracked = isTracked) }
    }

    fun onWindCardToggled(isTracked: Boolean) {
        settings.putBoolean("isWindCardTracked", isTracked)
        _uiState.update { it.copy(isWindCardTracked = isTracked) }
    }

    fun onPooCardToggled(isTracked: Boolean) {
        settings.putBoolean("isPooCardTracked", isTracked)
        _uiState.update { it.copy(isPooCardTracked = isTracked) }
    }

    suspend fun onEnableBottleNotificationToggled(isEnabled: Boolean) {
        settings.putBoolean("enableBottleNotification", isEnabled)
        _uiState.update { it.copy(enableBottleNotification = isEnabled) }
        if (isEnabled) {
            bottleNotificationController.requestNotificationAccessByUser()
        }
    }

    fun onSettingsComplete() {
        // Mark this step as complete if necessary for onboarding flow
        settings.putBoolean("isCardSelectionOnboardingComplete", true)
    }
}