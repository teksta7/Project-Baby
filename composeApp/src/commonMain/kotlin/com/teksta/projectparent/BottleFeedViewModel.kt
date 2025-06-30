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
        loadFeeds()
        loadAnalytics()
    }
    
    fun loadFeeds() {
        viewModelScope.launch {
            val dbFeeds = repository.getAllFeeds()
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
        }
    }
    
    fun startTimer() {
        if (ounces <= 0.0) {
            showWarningAlert = true
            return
        }
        
        if (!isTimerRunning) {
            startTime = Clock.System.now().epochSeconds
            bottleDuration = 0
            isTimerRunning = true
            buttonLabel = "Finish Bottle Feed"
            buttonColor = BottleFeedButtonColor.ORANGE
            
            timerJob = viewModelScope.launch {
                while (isTimerRunning) {
                    delay(1000)
                    bottleDuration++
                }
            }
        }
    }
    
    fun stopTimer() {
        if (isTimerRunning) {
            timerJob?.cancel()
            isTimerRunning = false
            
            val endTime = Clock.System.now().epochSeconds
            val duration = endTime - (startTime ?: endTime)
            
            addBottleFeed(duration.toDouble(), endTime)
            
            // Reset UI
            buttonLabel = "Start Bottle Feed"
            buttonColor = BottleFeedButtonColor.GREEN
            ounces = 0.0
            notes = ""
            bottleDuration = 0
            startTime = null
        }
    }
    
    private fun addBottleFeed(duration: Double, endTime: Long) {
        viewModelScope.launch {
            try {
                val startTimeValue = startTime ?: return@launch
                repository.insertFeed(
                    startTime = startTimeValue,
                    endTime = endTime,
                    duration = duration,
                    ounces = ounces,
                    additionalNotes = notes.takeIf { it.isNotBlank() }
                )
                
                showSuccessAlert = true
                loadFeeds()
                loadAnalytics()
                
                // Auto-hide success alert
                delay(2000)
                showSuccessAlert = false
                
            } catch (e: Exception) {
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
        
        val timeUntil = nextBottleTime - now
        val hours = (timeUntil / 3600).toInt()
        val minutes = ((timeUntil % 3600) / 60).toInt()
        
        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
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