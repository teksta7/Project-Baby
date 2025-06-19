package com.teksta.projectparent.models

import com.russhwolf.settings.Settings
import com.teksta.projectparent.services.BottleFeedTrackerManager
import com.teksta.projectparent.services.BottleNotificationController
import com.teksta.projectparent.services.InAppReviewManager
import com.teksta.projectparent.services.ScreenIdleManager
import com.teksta.projectparent.services.getPlatformContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock

/**
 * Represents the entire state for the BottlesScreen UI. This is observed by the composable.
 */
data class BottlesUiState(
    val bottlesTakenToday: Int = 0,
    val ouncesToSave: Double = 1.0,
    val notesToSave: String = "",
    val bottleDurationSeconds: Int = 0,
    val isTimerRunning: Boolean = false,
    val bottleFeedButtonLabel: String = "Start Bottle Feed",
    val bottleFeedButtonColorValue: Long = 0xFF4CAF50, // Green
    val showOuncesStepperDialog: Boolean = false,
    val showBottleListScreen: Boolean = false,
    val alertMessage: String? = null,
    val bottlesBeforeReview: Int = 10
)

class BottlesViewModel(
    // In a real app, these would be injected using a DI library like Koin
    private val settings: Settings = Settings(),
    // TODO: private val bottleRepository: BottleRepository,
    private val bottleNotificationController: BottleNotificationController = BottleNotificationController(getPlatformContext()),
    private val bottleFeedTrackerManager: BottleFeedTrackerManager = BottleFeedTrackerManager(getPlatformContext()),
    private val inAppReviewManager: InAppReviewManager = InAppReviewManager(getPlatformContext()),
    private val screenIdleManager: ScreenIdleManager = ScreenIdleManager(getPlatformContext())
) {
    private val _uiState = MutableStateFlow(BottlesUiState())
    val uiState = _uiState.asStateFlow()

    private var startTimeMillis: Long = 0L

    init {
        loadInitialState()
    }

    private fun loadInitialState() {
        _uiState.update {
            it.copy(
                ouncesToSave = settings.getDouble("setDefaultOunces", 1.0),
                notesToSave = settings.getString("DefaultBottleNote", ""),
                // bottlesTakenToday = bottleRepository.getBottlesTakenTodayCount(),
                bottlesBeforeReview = settings.getInt("bottlesBeforeReview", 10)
            )
        }
        // TODO: Add logic here to check if the tracking service is already running when the app starts.
        // This would involve a platform-specific check. If it is running, you can immediately
        // update `isTimerRunning = true` and other UI elements.
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
        _uiState.update { it.copy(showBottleListScreen = true) }
    }

    fun bottleListNavigationComplete() {
        _uiState.update { it.copy(showBottleListScreen = false) }
    }

    fun startOrFinishBottleFeed() {
        if (_uiState.value.isTimerRunning) {
            // --- FINISH BOTTLE FEED ---
            bottleFeedTrackerManager.stopTracking()
            screenIdleManager.allowScreenToDim()

            // Update UI state immediately to reflect that the timer has stopped
            _uiState.update {
                it.copy(
                    isTimerRunning = false,
                    bottleDurationSeconds = 0,
                    bottleFeedButtonLabel = "Start Bottle Feed",
                    bottleFeedButtonColorValue = 0xFF4CAF50, // Green
                    alertMessage = "Bottle recorded successfully!",
                    bottlesBeforeReview = it.bottlesBeforeReview - 1
                )
            }
            settings.putInt("bottlesBeforeReview", _uiState.value.bottlesBeforeReview)

            if (_uiState.value.bottlesBeforeReview <= 0) {
                inAppReviewManager.requestReview()
                settings.putInt("bottlesBeforeReview", 10) // Reset
            }

            // TODO: Create a Bottle data object and save it to your KMP database via a repository.

        } else {
            // --- START BOTTLE FEED ---
            if (_uiState.value.ouncesToSave <= 0.0) {
                _uiState.update { it.copy(alertMessage = "Please set ounces greater than 0.") }
                return
            }

            startTimeMillis = Clock.System.now().toEpochMilliseconds()

            // Update the UI state immediately for instant user feedback
            _uiState.update {
                it.copy(
                    isTimerRunning = true,
                    bottleDurationSeconds = 0,
                    bottleFeedButtonLabel = "Finish Bottle Feed",
                    bottleFeedButtonColorValue = 0xFFFFA500 // Orange
                )
            }

            // Perform the background tasks after updating the UI
            screenIdleManager.keepScreenOn()
            bottleFeedTrackerManager.startTracking(
                babyName = settings.getString("babyName", "Baby"),
                estimatedEndTimeStamp = 0L, // This can be refined later if you wish
                startTimeStamp = startTimeMillis
            )
        }
    }

    /**
     * Called by the BroadcastReceiver in the UI to update the timer state
     * with the latest value from the running BottleTrackingService.
     */
    fun updateDurationFromBroadcast(durationSeconds: Int) {
        // This ensures the UI state is correct if it's being restored while a feed is in progress.
        _uiState.update {
            it.copy(
                bottleDurationSeconds = durationSeconds,
                isTimerRunning = true,
                bottleFeedButtonLabel = "Finish Bottle Feed",
                bottleFeedButtonColorValue = 0xFFFFA500 // Orange
            )
        }
    }

    /**
     * Formats the total seconds into a MM:SS string for display in the UI.
     */
    fun formatDuration(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun dismissAlert() {
        _uiState.update { it.copy(alertMessage = null) }
    }

    /**
     * Called when the UI is disposed to clean up resources like the screen idle lock.
     */
    fun onDispose() {
        screenIdleManager.allowScreenToDim()
    }
}