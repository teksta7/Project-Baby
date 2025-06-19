package com.teksta.projectparent.onboarding

// In sharedFramework/src/commonMain/kotlin/com/teksta/projectparent/onboarding/BabyProfileViewModel.kt

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.* // Kotlinx-datetime for multiplatform date handling

// State for the UI to observe
data class BabyProfileUiState(
    val babyName: String = "",
    val gender: String = "Boy",
    val birthDate: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
    val weightPounds: Int = 0,
    val weightOunces: Int = 0,
    val selectedImageUri: String? = null, // We'll store the image URI as a string
    val validationError: String? = null,
    val isSaved: Boolean = false
)

class BabyProfileViewModel {
    private val settings: Settings = Settings()
    // In a real app, you would inject a repository here to talk to your SQLDelight database
    // private val profileRepository: ProfileRepository

    private val _uiState = MutableStateFlow(BabyProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChanged(newName: String) {
        _uiState.update { it.copy(babyName = newName, validationError = null) }
    }

    fun onGenderSelected(newGender: String) {
        _uiState.update { it.copy(gender = newGender) }
    }

    fun onBirthDateSelected(newDateMillis: Long) {
        val instant = Instant.fromEpochMilliseconds(newDateMillis)
        val localDate = instant.toLocalDateTime(TimeZone.UTC).date
        _uiState.update { it.copy(birthDate = localDate) }
    }

    fun onWeightPoundsChanged(pounds: Int) {
        _uiState.update { it.copy(weightPounds = pounds) }
    }

    fun onWeightOuncesChanged(ounces: Int) {
        _uiState.update { it.copy(weightOunces = ounces) }
    }

    fun onImageSelected(uri: String?) {
        _uiState.update { it.copy(selectedImageUri = uri, validationError = null) }
    }

    fun onSaveProfile() {
        val currentState = _uiState.value
        if (currentState.babyName.isBlank()) {
            _uiState.update { it.copy(validationError = "You have not entered a name") }
            return
        }
        if (currentState.selectedImageUri == null) {
            _uiState.update { it.copy(validationError = "You have not selected a profile picture") }
            return
        }

        // --- Save the data using multiplatform libraries ---
        settings.putString("babyName", currentState.babyName)
        settings.putString("babyGender", currentState.gender)
        settings.putString("babyBirthDate", currentState.birthDate.toString())

        // You would also save the image using a multiplatform file storage solution
        // and save the weight to your SQLDelight database.

        // Mark profile as complete in settings
        settings.putBoolean("isBabyProfileOnboardingComplete", true)

        _uiState.update { it.copy(isSaved = true) }
    }
}