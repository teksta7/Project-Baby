package com.teksta.projectparent.onboarding

// In commonMain/com/projectparent/onboarding/OnboardingViewModel.kt

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// A helper class to manage state
data class OnboardingUiState(
    val showWelcomeOnboarding: Boolean = true,
    val isBabyProfileOnboardingComplete: Boolean = false,
    val isCardSelectionOnboardingComplete: Boolean = false,
    val showBabyProfileSheet: Boolean = false,
    val showCardSelectionSheet: Boolean = false,
    val showAlert: AlertInfo? = null
)

data class AlertInfo(val type: AlertType, val title: String, val message: String)
enum class AlertType { SUCCESS, WARNING, ERROR }

class OnboardingViewModel {
    private val settings: Settings = Settings() // Multiplatform settings instance

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Load initial state from settings
        _uiState.update {
            it.copy(
                showWelcomeOnboarding = settings.getBoolean("showWelcomeOnboarding", true),
                isBabyProfileOnboardingComplete = settings.getBoolean("isBabyProfileOnboardingComplete", false),
                isCardSelectionOnboardingComplete = settings.getBoolean("isCardSelectionOnboardingComplete", false)
            )
        }
    }

    // Public function to re-check settings and update UI state
    fun refreshOnboardingStepStatuses() {
        val babyComplete = settings.getBoolean("isBabyProfileOnboardingComplete", false)
        val cardsComplete = settings.getBoolean("isCardSelectionOnboardingComplete", false)
        _uiState.update {
            it.copy(
                showWelcomeOnboarding = settings.getBoolean("showWelcomeOnboarding", true), // Also refresh this
                isBabyProfileOnboardingComplete = babyComplete,
                isCardSelectionOnboardingComplete = cardsComplete
            )
        }
        println("OnboardingViewModel: Refreshed. Baby Complete: $babyComplete, Cards Complete: $cardsComplete")
    }


    fun onBabyProfileClicked() {
        _uiState.update { it.copy(showBabyProfileSheet = true) }
    }

    fun onCardSelectionClicked() {
        _uiState.update { it.copy(showCardSelectionSheet = true) }
    }

    fun onBabyProfileOnboardingDismiss() {
        // This would be called when the baby profile sheet is closed
        //val isComplete = settings.getBoolean("isBabyProfileOnboardingComplete", false)
        settings.putBoolean("isBabyProfileOnboardingComplete", true)
        _uiState.update { it.copy(showBabyProfileSheet = false, isBabyProfileOnboardingComplete = true) }
    }

    fun onCardSelectionOnboardingDismiss() {
        // This would be called when the card selection sheet is closed
        // For simplicity, we assume it's always complete once opened in this example
        settings.putBoolean("isCardSelectionOnboardingComplete", true)
        _uiState.update { it.copy(showCardSelectionSheet = false, isCardSelectionOnboardingComplete = true) }
    }

    fun onFinishClicked() {
        if (uiState.value.isBabyProfileOnboardingComplete && uiState.value.isCardSelectionOnboardingComplete) {
            settings.putBoolean("showWelcomeOnboarding", false)
            _uiState.update { it.copy(
                showAlert = AlertInfo(AlertType.SUCCESS, "Success", "App is now ready to use"),
            )}
            // The UI would then navigate away after a delay
        } else {
            _uiState.update { it.copy(
                showAlert = AlertInfo(AlertType.WARNING, "Incomplete setup", "One or more sections are incomplete"),
            )}
        }
    }

    fun onAlertDismissed() {
        if (_uiState.value.showAlert?.type == AlertType.SUCCESS) {
            _uiState.update { it.copy(showAlert = null, showWelcomeOnboarding = false) }
        } else {
            _uiState.update { it.copy(showAlert = null) }
        }
    }
}