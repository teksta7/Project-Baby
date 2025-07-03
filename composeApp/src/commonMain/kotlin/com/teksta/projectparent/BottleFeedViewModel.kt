package com.teksta.projectparent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.teksta.projectparent.db.BottleFeedRepository
import com.teksta.projectparent.db.BottleFeed
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlin.math.pow
import kotlin.math.round
import com.russhwolf.settings.Settings
import android.content.SharedPreferences

// Simple logging utility
expect fun logDebug(tag: String, message: String)

// Foreground service hooks for Android
// expect fun startBottleFeedForegroundService(babyName: String, elapsed: Int, total: Int, average: Int)
// expect fun updateBottleFeedForegroundService(elapsed: Int, total: Int, average: Int)
expect fun stopBottleFeedForegroundService()

class BottleFeedViewModel(private val repository: BottleFeedRepository) {
    
    // UI State
    var feeds by mutableStateOf<List<BottleFeedUiModel>>(emptyList())
        private set
    var analytics by mutableStateOf(BottleFeedAnalyticsUiModel())
        private set
    
    // Timer State
    var isTimerRunning by mutableStateOf(false)
        private set
    var bottleDuration by mutableStateOf(0)
        private set
    var startTime by mutableStateOf<Long?>(null)
        private set
    
    // Form State
    var ounces by mutableStateOf(0.0)
    var notes by mutableStateOf("")
    
    // Alert State
    var showSuccessAlert by mutableStateOf(false)
        private set
    var showWarningAlert by mutableStateOf(false)
        private set
    var showErrorAlert by mutableStateOf(false)
        private set
    
    // Button State
    var buttonLabel by mutableStateOf("Start Bottle Feed")
        private set
    var buttonColor by mutableStateOf(BottleFeedButtonColor.GREEN)
        private set
    
    var todayFeeds by mutableStateOf<List<BottleFeedUiModel>>(emptyList())
        private set
    var yesterdayFeeds by mutableStateOf<List<BottleFeedUiModel>>(emptyList())
        private set
    var olderFeeds by mutableStateOf<List<BottleFeedUiModel>>(emptyList())
        private set
    
    private val viewModelScope = CoroutineScope(Dispatchers.Default + Job())
    private var timerJob: Job? = null
    private val settings = Settings()
    private val inProgressKey = "bottle_in_progress"
    private val startTimeKey = "bottle_start_time"
    private val durationKey = "bottle_duration"
    private val ouncesKey = "bottle_ounces"
    private val notesKey = "bottle_notes"
    
    init {
        logDebug("BottleFeedViewModel", "Initializing BottleFeedViewModel")
        loadFeeds()
        loadAnalytics()
        // Check for external finish/cancel immediately
        checkExternalFinishOrCancel()
        // Restore in-progress state if present
        if (settings.getBoolean(inProgressKey, false)) {
            startTime = settings.getLongOrNull(startTimeKey)
            bottleDuration = settings.getInt(durationKey, 0)
            ounces = settings.getString(ouncesKey, "0.0").toDoubleOrNull() ?: 0.0
            notes = settings.getString(notesKey, "")
            isTimerRunning = true
            buttonLabel = "Finish Bottle Feed"
            buttonColor = BottleFeedButtonColor.ORANGE
            // Only start timer if bottle_in_progress is still true
            if (settings.getBoolean(inProgressKey, false)) {
                logDebug("BottleFeedViewModel", "Starting timer job")
                timerJob = viewModelScope.launch {
                    while (isTimerRunning) {
                        delay(1000)
                        logDebug("BottleFeedViewModel", "Timer job tick: bottle_in_progress=${settings.getBoolean(inProgressKey, false)}")
                        // Check for external finish/cancel
                        if (settings.getBoolean("bottle_feed_finished_externally", false)) {
                            logDebug("BottleFeedViewModel", "Detected external finish/cancel, stopping timer.")
                            stopTimer(fromExternal = true)
                            settings.putBoolean("bottle_feed_finished_externally", false)
                            return@launch
                        }
                        // Check if bottle_in_progress is still true
                        if (!settings.getBoolean(inProgressKey, false)) {
                            logDebug("BottleFeedViewModel", "bottle_in_progress is false, stopping timer and resetting state.")
                            stopTimer(fromExternal = true)
                            return@launch
                        }
                        bottleDuration++
                        // Only update foreground service if not externally finished
                        if (isTimerRunning) {
                            val avgDuration = analytics.averageBottleDuration.toDurationSecondsOrNull() ?: getBottleFeedTotalDuration()
                            updateBottleFeedForegroundService(bottleDuration, getBottleFeedTotalDuration(), avgDuration)
                        }
                    }
                }
            } else {
                logDebug("BottleFeedViewModel", "bottle_in_progress is false on init, not starting timer.")
                isTimerRunning = false
                buttonLabel = "Start Bottle Feed"
                buttonColor = BottleFeedButtonColor.GREEN
            }
        } else {
            // Set to default values if not restoring
            ounces = settings.getString("default_ounces", "4.0").toDoubleOrNull() ?: 4.0
            notes = settings.getString("default_bottle_note", "")
        }
    }
    
    fun loadFeeds() {
        viewModelScope.launch {
            logDebug("BottleFeedViewModel", "Loading feeds from database")
            val todayRaw = repository.getTodayFeeds()
            val yesterdayRaw = repository.getYesterdayFeeds()
            val allRaw = repository.getAllFeeds()
            val todayIds = todayRaw.map { it.id }.toSet()
            val yesterdayIds = yesterdayRaw.map { it.id }.toSet()
            val olderRaw = allRaw.filter { it.id !in todayIds && it.id !in yesterdayIds }

            todayFeeds = todayRaw.map { feed ->
                BottleFeedUiModel(
                    id = feed.id,
                    ounces = feed.ounces,
                    startTime = feed.start_time,
                    endTime = feed.end_time,
                    duration = feed.duration,
                    notes = feed.additional_notes ?: "",
                    date = feed.date
                )
            }
            yesterdayFeeds = yesterdayRaw.map { feed ->
                BottleFeedUiModel(
                    id = feed.id,
                    ounces = feed.ounces,
                    startTime = feed.start_time,
                    endTime = feed.end_time,
                    duration = feed.duration,
                    notes = feed.additional_notes ?: "",
                    date = feed.date
                )
            }
            olderFeeds = olderRaw.map { feed ->
                BottleFeedUiModel(
                    id = feed.id,
                    ounces = feed.ounces,
                    startTime = feed.start_time,
                    endTime = feed.end_time,
                    duration = feed.duration,
                    notes = feed.additional_notes ?: "",
                    date = feed.date
                )
            }
            logDebug("BottleFeedViewModel", "todayFeeds: ${todayFeeds.size}, yesterdayFeeds: ${yesterdayFeeds.size}, olderFeeds: ${olderFeeds.size}")
        }
    }
    
    fun loadAnalytics() {
        viewModelScope.launch {
            val bottlesToday = repository.getBottlesTakenToday()
            val averageDuration = repository.getAverageBottleDuration()
            val averageTimeBetween = repository.getAverageTimeBetweenBottles()
            val totalBottles = repository.getTotalBottles()
            
            analytics = BottleFeedAnalyticsUiModel(
                bottlesTakenToday = bottlesToday,
                averageBottleDuration = formatDuration(averageDuration),
                averageTimeBetweenBottles = formatDuration(averageTimeBetween),
                totalBottles = totalBottles,
                nextBottleDue = calculateNextBottleDue(averageTimeBetween)
            )
            
            logDebug("BottleFeedViewModel", "Analytics updated: bottlesToday=$bottlesToday, totalBottles=$totalBottles")
        }
    }
    
    fun startTimer() {
        logDebug("BottleFeedViewModel", "startTimer called - ounces: $ounces, isTimerRunning: $isTimerRunning")
        
        if (ounces <= 0.0) {
            logDebug("BottleFeedViewModel", "Warning: ounces is $ounces, showing warning alert")
            showWarningAlert = true
            return
        }
        
        if (!isTimerRunning) {
            startTime = Clock.System.now().epochSeconds
            bottleDuration = 0
            isTimerRunning = true
            buttonLabel = "Finish Bottle Feed"
            buttonColor = BottleFeedButtonColor.ORANGE
            
            logDebug("BottleFeedViewModel", "Timer started - startTime: $startTime")
            
            val avgDuration = analytics.averageBottleDuration.toDurationSecondsOrNull() ?: getBottleFeedTotalDuration()
            startBottleFeedForegroundService("Baby", 0, getBottleFeedTotalDuration(), avgDuration)
            
            timerJob = viewModelScope.launch {
                while (isTimerRunning) {
                    delay(1000)
                    bottleDuration++
                    updateBottleFeedForegroundService(bottleDuration, getBottleFeedTotalDuration(), avgDuration)
                }
            }
        }
        // Persist state
        settings.putBoolean(inProgressKey, true)
        settings.putLong(startTimeKey, startTime ?: 0L)
        settings.putInt(durationKey, bottleDuration)
        settings.putString(ouncesKey, ounces.toString())
        settings.putString(notesKey, notes)
    }
    
    fun stopTimer(fromExternal: Boolean = false, saveFeed: Boolean = false) {
        logDebug("BottleFeedViewModel", "stopTimer called - isTimerRunning: $isTimerRunning, fromExternal: $fromExternal, saveFeed: $saveFeed")
        
        if (isTimerRunning) {
            timerJob?.cancel()
            isTimerRunning = false
            
            val endTime = Clock.System.now().epochSeconds
            val duration = endTime - (startTime ?: endTime)
            
            // Capture current values before resetting
            val currentOunces = ounces
            val currentNotes = notes
            
            logDebug("BottleFeedViewModel", "Timer stopped - duration: $duration, ounces: $currentOunces, notes: '$currentNotes', fromExternal: $fromExternal, saveFeed: $saveFeed")
            if (!fromExternal || saveFeed) {
                addBottleFeed(duration.toDouble(), endTime, currentOunces, currentNotes)
            }
            // Reset all UI state and prevent further notification updates
            buttonLabel = "Start Bottle Feed"
            buttonColor = BottleFeedButtonColor.GREEN
            ounces = settings.getString("default_ounces", "4.0").toDoubleOrNull() ?: 4.0
            notes = settings.getString("default_bottle_note", "")
            bottleDuration = 0
            startTime = null
            logDebug("BottleFeedViewModel", "State fully reset after stop.")
            // Stop foreground service on Android
            stopBottleFeedForegroundService()
        }
        // Clear persisted state
        settings.putBoolean(inProgressKey, false)
        settings.remove(startTimeKey)
        settings.remove(durationKey)
        settings.remove(ouncesKey)
        settings.remove(notesKey)
    }
    
    private fun addBottleFeed(duration: Double, endTime: Long, feedOunces: Double, feedNotes: String) {
        viewModelScope.launch {
            try {
                val startTimeValue = startTime ?: return@launch
                logDebug("BottleFeedViewModel", "Saving bottle feed - startTime: $startTimeValue, endTime: $endTime, duration: $duration, ounces: $feedOunces, notes: '$feedNotes'")
                
                repository.insertFeed(
                    startTime = startTimeValue,
                    endTime = endTime,
                    duration = duration,
                    ounces = feedOunces,
                    additionalNotes = feedNotes
                )
                
                logDebug("BottleFeedViewModel", "Bottle feed saved successfully")
                
                showSuccessAlert = true
                loadFeeds()
                loadAnalytics()
                
                // Reset UI after successful save
                buttonLabel = "Start Bottle Feed"
                buttonColor = BottleFeedButtonColor.GREEN
                ounces = settings.getString("default_ounces", "4.0").toDoubleOrNull() ?: 4.0
                notes = settings.getString("default_bottle_note", "")
                bottleDuration = 0
                startTime = null
                
                logDebug("BottleFeedViewModel", "UI reset after successful save")
                
                // Auto-hide success alert
                delay(2000)
                showSuccessAlert = false
                
            } catch (e: Exception) {
                logDebug("BottleFeedViewModel", "Error saving bottle feed: ${e.message}")
                showErrorAlert = true
                buttonColor = BottleFeedButtonColor.RED
                buttonLabel = "Error"
                
                // Auto-hide error alert
                delay(2000)
                showErrorAlert = false
                buttonColor = BottleFeedButtonColor.GREEN
                buttonLabel = "Start Bottle Feed"
            }
        }
    }
    
    fun deleteFeed(id: String) {
        viewModelScope.launch {
            logDebug("BottleFeedViewModel", "Deleting feed with ID: $id")
            repository.deleteFeed(id)
            loadFeeds()
            loadAnalytics()
        }
    }
    
    fun dismissAlerts() {
        showSuccessAlert = false
        showWarningAlert = false
        showErrorAlert = false
    }
    
    private fun formatDuration(seconds: Double): String {
        if (seconds <= 0) return "N/A"
        val minutes = (seconds / 60).toInt()
        val remainingSeconds = (seconds % 60).toInt()
        return "${minutes}m ${remainingSeconds}s"
    }
    
    private fun calculateNextBottleDue(averageTimeBetween: Double): String {
        if (averageTimeBetween <= 0) return "N/A"
        
        val lastBottle = feeds.firstOrNull()
        if (lastBottle == null) return "N/A"
        
        val nextBottleTime = lastBottle.startTime + averageTimeBetween.toLong()
        val now = Clock.System.now().epochSeconds
        
        if (nextBottleTime <= now) return "Due now"
        
        // Format as local time (e.g., 5:00pm)
        val localTime = Instant.fromEpochSeconds(nextBottleTime)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val hour = localTime.hour
        val minute = localTime.minute
        val ampm = if (hour < 12) "am" else "pm"
        val hour12 = if (hour == 0 || hour == 12) 12 else hour % 12
        val minuteStr = minute.toString().padStart(2, '0')
        return "$hour12:$minuteStr$ampm"
    }
    
    fun cleanup() {
        timerJob?.cancel()
        viewModelScope.cancel()
    }
    
    private fun getBottleFeedTotalDuration(): Int {
        // Use the duration from settings if available, fallback to 180 min (3 hours)
        // This should be replaced with actual settings retrieval if needed
        return 180 * 60 // seconds
    }
    
    // Call this from the Composable's LaunchedEffect to check on every screen resume
    fun checkExternalFinishOrCancel() {
        if (settings.getBoolean("bottle_feed_finished_externally", false)) {
            logDebug("BottleFeedViewModel", "Detected external finish/cancel (from Composable), stopping timer.")
            stopTimer(fromExternal = true)
            settings.putBoolean("bottle_feed_finished_externally", false)
        }
    }
    
    // Call this from Android code when the bottle_feed_action preference changes
    fun onBottleFeedActionChanged(action: String) {
        logDebug("BottleFeedViewModel", "onBottleFeedActionChanged called with action: $action")
        when (action) {
            "finish" -> {
                logDebug("BottleFeedViewModel", "Detected finish action, calling stopTimer with saveFeed = true")
                stopTimer(fromExternal = true, saveFeed = true)
            }
            "cancel" -> {
                logDebug("BottleFeedViewModel", "Detected cancel action, calling stopTimer with saveFeed = false")
                stopTimer(fromExternal = true, saveFeed = false)
            }
        }
    }
    
    // Call this from the Composable's LaunchedEffect to check on every screen resume and reload timer state
    fun reloadTimerStateFromSettings() {
        logDebug("BottleFeedViewModel", "reloadTimerStateFromSettings called")
        if (settings.getBoolean(inProgressKey, false)) {
            startTime = settings.getLongOrNull(startTimeKey)
            val now = Clock.System.now().epochSeconds
            bottleDuration = if (startTime != null) (now - startTime!!).toInt() else 0
            ounces = settings.getString(ouncesKey, "0.0").toDoubleOrNull() ?: 0.0
            notes = settings.getString(notesKey, "")
            isTimerRunning = true
            buttonLabel = "Finish Bottle Feed"
            buttonColor = BottleFeedButtonColor.ORANGE
            logDebug("BottleFeedViewModel", "Restored in-progress feed: startTime=$startTime, bottleDuration=$bottleDuration (calculated), ounces=$ounces, notes=$notes")
            // Only start timer if not already running
            if (timerJob == null || !timerJob!!.isActive) {
                logDebug("BottleFeedViewModel", "Starting timer job from reload")
                timerJob = viewModelScope.launch {
                    while (isTimerRunning) {
                        delay(1000)
                        logDebug("BottleFeedViewModel", "Timer job tick (reload): bottle_in_progress=${settings.getBoolean(inProgressKey, false)}")
                        if (!settings.getBoolean(inProgressKey, false)) {
                            logDebug("BottleFeedViewModel", "bottle_in_progress is false, stopping timer and resetting state (reload)")
                            stopTimer(fromExternal = true)
                            return@launch
                        }
                        bottleDuration = if (startTime != null) (Clock.System.now().epochSeconds - startTime!!).toInt() else bottleDuration + 1
                        val avgDuration = analytics.averageBottleDuration.toDurationSecondsOrNull() ?: getBottleFeedTotalDuration()
                        updateBottleFeedForegroundService(bottleDuration, getBottleFeedTotalDuration(), avgDuration)
                    }
                }
            }
        } else {
            isTimerRunning = false
            buttonLabel = "Start Bottle Feed"
            buttonColor = BottleFeedButtonColor.GREEN
            bottleDuration = 0
            startTime = null
            logDebug("BottleFeedViewModel", "No in-progress feed found on reload")
        }
    }
}

// UI Models
data class BottleFeedUiModel(
    val id: String,
    val ounces: Double,
    val startTime: Long,
    val endTime: Long,
    val duration: Double,
    val notes: String,
    val date: Long
) {
    val formattedStartTime: String
        get() = Instant.fromEpochSeconds(startTime)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toString()
    
    val formattedDuration: String
        get() {
            val minutes = (duration / 60).toInt()
            val seconds = (duration % 60).toInt()
            return "${minutes}m ${seconds}s"
        }
}

data class BottleFeedAnalyticsUiModel(
    val bottlesTakenToday: Int = 0,
    val averageBottleDuration: String = "N/A",
    val averageTimeBetweenBottles: String = "N/A",
    val totalBottles: Int = 0,
    val nextBottleDue: String = "N/A"
)

enum class BottleFeedButtonColor {
    GREEN, ORANGE, RED
}

// Helper extension to parse duration string like "3m 20s" to seconds
fun String.toDurationSecondsOrNull(): Int? {
    val regex = Regex("(\\d+)m\\s*(\\d+)s")
    val match = regex.find(this) ?: return null
    val (min, sec) = match.destructured
    return min.toInt() * 60 + sec.toInt()
} 