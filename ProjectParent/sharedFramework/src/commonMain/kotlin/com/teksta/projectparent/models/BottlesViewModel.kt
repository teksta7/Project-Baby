package com.teksta.projectparent.models

import com.russhwolf.settings.Settings
import com.teksta.projectparent.services.BottleFeedTrackerManager
import com.teksta.projectparent.services.BottleNotificationController
import com.teksta.projectparent.services.InAppReviewManager
import com.teksta.projectparent.services.ScreenIdleManager
import com.teksta.projectparent.services.getPlatformContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

// Represents the state for the BottlesScreen UI
data class BottlesUiState(
    val bottlesTakenToday: Int = 0, // Will be loaded from a BottleRepository
    val ouncesToSave: Double = 1.0, // Default from settings
    val notesToSave: String = "",   // Default from settings
    val bottleDurationSeconds: Int = 0,
    val isTimerRunning: Boolean = false,
    val bottleFeedButtonLabel: String = "Start Bottle Feed",
    val bottleFeedButtonColorValue: Long = 0xFF4CAF50, // Green
    val showOuncesStepperDialog: Boolean = false,
    val showBottleListScreen: Boolean = false, // Will trigger navigation
    val alertMessage: String? = null, // For simple feedback
    val bottlesBeforeReview: Int = 10 // Example
)

class BottlesViewModel(
    // In a real app, these would be injected using DI (e.g., Koin)
    private val settings: Settings = Settings(),
    // private val bottleRepository: BottleRepository, // For DB operations
    private val bottleNotificationController: BottleNotificationController, // From onboarding
    private val bottleFeedTrackerManager: BottleFeedTrackerManager = BottleFeedTrackerManager(
        getPlatformContext()
    ),
    private val inAppReviewManager: InAppReviewManager = InAppReviewManager(getPlatformContext()),
    private val screenIdleManager: ScreenIdleManager = ScreenIdleManager(getPlatformContext())
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null
    private var startTimeMillis: Long = 0L

    private val _uiState = MutableStateFlow(BottlesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialState()
        // observeBottleTimerController() // If using a separate timer controller
    }

    private fun loadInitialState() {
        _uiState.update {
            it.copy(
                ouncesToSave = settings.getDouble("setDefaultOunces", 1.0),
                notesToSave = settings.getString("DefaultBottleNote", ""),
                // bottlesTakenToday = bottleRepository.getBottlesTakenTodayCount()
                bottlesBeforeReview = settings.getInt("bottlesBeforeReview", 10)
            )
        }
    }

    fun onOuncesChanged(ounces: Double) {
        _uiState.update { it.copy(ouncesToSave = ounces) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notesToSave = notes) }
    }

    fun toggleOuncesDialog(show: Boolean) {
        _uiState.update { it.copy(showOuncesStepperDialog = show) }
    }

    fun requestBottleListNavigation() {
        // This will be observed by the UI to trigger navigation
        _uiState.update { it.copy(showBottleListScreen = true) }
    }
    fun bottleListNavigationComplete() {
        _uiState.update { it.copy(showBottleListScreen = false) }
    }


    fun startOrFinishBottleFeed() {
        if (_uiState.value.isTimerRunning) {
            // --- Finish Bottle Feed ---
            stopTimer()
            screenIdleManager.allowScreenToDim()
            bottleFeedTrackerManager.stopTracking()

            val endTimeMillis = Clock.System.now().toEpochMilliseconds()
            val durationSeconds = ((endTimeMillis - startTimeMillis) / 1000).toInt()

            // TODO: Create Bottle object and save to repository
            // val newBottle = Bottle(
            //     id = uuid4().toString(),
            //     date = Clock.System.now(),
            //     startTime = Instant.fromEpochMilliseconds(startTimeMillis),
            //     endTime = Instant.fromEpochMilliseconds(endTimeMillis),
            //     durationSeconds = durationSeconds,
            //     ounces = _uiState.value.ouncesToSave,
            //     additionalNotes = _uiState.value.notesToSave
            // )
            // viewModelScope.launch { bottleRepository.addBottle(newBottle) }

            _uiState.update {
                it.copy(
                    bottleFeedButtonLabel = "Start Bottle Feed",
                    bottleFeedButtonColorValue = 0xFF4CAF50, // Green
                    alertMessage = "Bottle recorded successfully!",
                    // bottlesTakenToday = bottleRepository.getBottlesTakenTodayCount(),
                    bottlesBeforeReview = it.bottlesBeforeReview - 1
                )
            }
            settings.putInt("bottlesBeforeReview", _uiState.value.bottlesBeforeReview)

            // Schedule notification
            // val latestBottleID = newBottle.id
            // viewModelScope.launch {
            //    bottleNotificationController.scheduleBottleNotification(
            //        settings.getInt("com.projectparent.localTimeBetweenFeeds", 0),
            //        latestBottleID
            //    )
            // }

            if (_uiState.value.bottlesBeforeReview <= 0) {
                inAppReviewManager.requestReview()
                settings.putInt("bottlesBeforeReview", 10) // Reset
            }

        } else {
            // --- Start Bottle Feed ---
            if (_uiState.value.ouncesToSave <= 0.0) {
                _uiState.update { it.copy(alertMessage = "Please set ounces greater than 0.") }
                return
            }
            startTimeMillis = Clock.System.now().toEpochMilliseconds()
            startTimer()
            screenIdleManager.keepScreenOn()
            // TODO: Get average bottle duration for estimated end time
            // val averageDurationMillis = settings.getLong("projectparent.averagebottleduration", 300_000L) // 5 mins default
            // bottleFeedTrackerManager.startTracking(
            //     babyName = settings.getString("projectparent.babyName", "Baby"),
            //     estimatedEndTimeStamp = startTimeMillis + averageDurationMillis,
            //     startTimeStamp = startTimeMillis
            // )

            _uiState.update {
                it.copy(
                    bottleFeedButtonLabel = "Finish Bottle Feed",
                    bottleFeedButtonColorValue = 0xFFFFA500 // Orange
                )
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isTimerRunning = true, bottleDurationSeconds = 0) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.isTimerRunning) {
                delay(1000)
                val newDuration = _uiState.value.bottleDurationSeconds + 1
                _uiState.update { it.copy(bottleDurationSeconds = newDuration) }
                bottleFeedTrackerManager.updateTracking(newDuration)
            }
        }
    }

    private fun stopTimer() {
        _uiState.update { it.copy(isTimerRunning = false) }
        timerJob?.cancel()
    }

    fun formatDuration(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun dismissAlert() {
        _uiState.update { it.copy(alertMessage = null) }
    }

    // Call this when the composable is disposed if screenIdleManager needs explicit release
    fun onDispose() {
        screenIdleManager.allowScreenToDim()
        timerJob?.cancel() // Ensure timer is stopped
    }
}