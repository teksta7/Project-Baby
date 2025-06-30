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

// Simple logging utility
expect fun logDebug(tag: String, message: String)

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
    
    private val viewModelScope = CoroutineScope(Dispatchers.Default + Job())
    private var timerJob: Job? = null
    
    init {
        logDebug("BottleFeedViewModel", "Initializing BottleFeedViewModel")
        loadFeeds()
        loadAnalytics()
    }
    
    fun loadFeeds() {
        viewModelScope.launch {
            logDebug("BottleFeedViewModel", "Loading feeds from database")
            val dbFeeds = repository.getAllFeeds()
            logDebug("BottleFeedViewModel", "Loaded ${dbFeeds.size} feeds from database")
            
            dbFeeds.forEach { feed ->
                logDebug("BottleFeedViewModel", "Feed: ID=${feed.id}, Ounces=${feed.ounces}, Notes='${feed.additional_notes}', Duration=${feed.duration}")
            }
            
            feeds = dbFeeds.map { feed ->
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
            
            logDebug("BottleFeedViewModel", "Mapped to ${feeds.size} UI models")
            feeds.forEach { uiModel ->
                logDebug("BottleFeedViewModel", "UI Model: ID=${uiModel.id}, Ounces=${uiModel.ounces}, Notes='${uiModel.notes}', Duration=${uiModel.duration}")
            }
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
            
            timerJob = viewModelScope.launch {
                while (isTimerRunning) {
                    delay(1000)
                    bottleDuration++
                }
            }
        }
    }
    
    fun stopTimer() {
        logDebug("BottleFeedViewModel", "stopTimer called - isTimerRunning: $isTimerRunning")
        
        if (isTimerRunning) {
            timerJob?.cancel()
            isTimerRunning = false
            
            val endTime = Clock.System.now().epochSeconds
            val duration = endTime - (startTime ?: endTime)
            
            // Capture current values before resetting
            val currentOunces = ounces
            val currentNotes = notes
            
            logDebug("BottleFeedViewModel", "Timer stopped - duration: $duration, ounces: $currentOunces, notes: '$currentNotes'")
            
            addBottleFeed(duration.toDouble(), endTime, currentOunces, currentNotes)
        }
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
                ounces = 0.0
                notes = ""
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