package com.teksta.projectparent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.teksta.projectparent.db.BottleFeedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant

class BottleFeedViewModel(private val repository: BottleFeedRepository) {
    var feeds by mutableStateOf<List<BottleFeedUiModel>>(emptyList())
        private set
    var analytics by mutableStateOf(BottleFeedAnalyticsUiModel())
        private set

    private val viewModelScope = CoroutineScope(Dispatchers.Default + Job())

    init {
        loadFeeds()
    }

    fun loadFeeds() {
        viewModelScope.launch {
            val dbFeeds = repository.getAllFeeds()
            feeds = dbFeeds.map {
                BottleFeedUiModel(
                    id = it.id,
                    amount = it.amount.toString(),
                    time = it.timestamp.toLocalDateTimeString(),
                    note = it.note ?: ""
                )
            }
            analytics = calculateAnalytics(feeds)
        }
    }

    fun addFeed(amount: String, note: String) {
        viewModelScope.launch {
            val amountDouble = amount.toDoubleOrNull() ?: return@launch
            val timestamp = Clock.System.now().epochSeconds
            repository.insertFeed(amountDouble, timestamp, note)
            loadFeeds()
        }
    }

    fun deleteFeed(id: Long) {
        viewModelScope.launch {
            repository.deleteFeed(id)
            loadFeeds()
        }
    }

    private fun calculateAnalytics(feeds: List<BottleFeedUiModel>): BottleFeedAnalyticsUiModel {
        if (feeds.isEmpty()) return BottleFeedAnalyticsUiModel()
        val amounts = feeds.mapNotNull { it.amount.toDoubleOrNull() }
        val averageAmount = if (amounts.isNotEmpty()) amounts.average().toStringAsFixed(2) else "-"
        val times = feeds.mapNotNull { it.time.toEpochSecondsOrNull() }.sorted()
        val averageTimeBetweenFeeds = if (times.size > 1) {
            val intervals = times.zipWithNext { a, b -> b - a }
            (intervals.average() / 60).toStringAsFixed(1) + " min"
        } else "-"
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val totalFeedsToday = feeds.count {
            it.time.toLocalDateOrNull() == today
        }.toString()
        return BottleFeedAnalyticsUiModel(
            averageAmount = averageAmount,
            averageTimeBetweenFeeds = averageTimeBetweenFeeds,
            totalFeedsToday = totalFeedsToday
        )
    }
}

// --- Helpers ---

private fun Long.toLocalDateTimeString(): String =
    kotlinx.datetime.Instant.fromEpochSeconds(this)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .toString()

private fun String.toEpochSecondsOrNull(): Long? =
    runCatching {
        Instant.parse(this).epochSeconds
    }.getOrNull()

private fun String.toLocalDateOrNull(): kotlinx.datetime.LocalDate? =
    runCatching {
        kotlinx.datetime.LocalDateTime.parse(this).date
    }.getOrNull()

private fun Double.toStringAsFixed(digits: Int): String =
    String.format("%.${'$'}df", this) 