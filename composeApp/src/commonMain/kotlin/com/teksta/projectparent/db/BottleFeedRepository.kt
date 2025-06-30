package com.teksta.projectparent.db

import com.teksta.projectparent.db.AppDatabaseWrapper
import com.teksta.projectparent.db.BottleFeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.atStartOfDayIn
import java.util.UUID

class BottleFeedRepository(private val database: AppDatabaseWrapper) {
    
    suspend fun getAllFeeds(): List<BottleFeed> = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.selectAll().executeAsList()
    }

    suspend fun getTodayFeeds(): List<BottleFeed> = withContext(Dispatchers.Default) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val todayStartEpoch = today.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds
        database.bottleFeedQueries.selectToday(todayStartEpoch).executeAsList()
    }

    suspend fun getYesterdayFeeds(): List<BottleFeed> = withContext(Dispatchers.Default) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val todayStartEpoch = today.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds
        val yesterdayStartEpoch = todayStartEpoch - (24 * 60 * 60) // Subtract 24 hours in seconds
        database.bottleFeedQueries.selectYesterday(yesterdayStartEpoch, todayStartEpoch).executeAsList()
    }

    suspend fun insertFeed(
        startTime: Long,
        endTime: Long,
        duration: Double,
        ounces: Double,
        additionalNotes: String?
    ): String = withContext(Dispatchers.Default) {
        val id = UUID.randomUUID().toString()
        val date = kotlinx.datetime.Clock.System.now().epochSeconds
        database.bottleFeedQueries.insertFeed(
            id = id,
            date = date,
            start_time = startTime,
            end_time = endTime,
            duration = duration,
            ounces = ounces,
            additional_notes = additionalNotes ?: ""
        )
        id
    }

    suspend fun updateFeed(
        id: String,
        startTime: Long,
        endTime: Long,
        duration: Double,
        ounces: Double,
        additionalNotes: String?
    ) = withContext(Dispatchers.Default) {
        val date = kotlinx.datetime.Clock.System.now().epochSeconds
        database.bottleFeedQueries.updateFeed(
            date = date,
            start_time = startTime,
            end_time = endTime,
            duration = duration,
            ounces = ounces,
            additional_notes = additionalNotes ?: "",
            id = id
        )
    }

    suspend fun deleteFeed(id: String) = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.deleteFeed(id)
    }

    suspend fun getBottlesTakenToday(): Int = withContext(Dispatchers.Default) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val todayStartEpoch = today.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds
        database.bottleFeedQueries.getBottlesTakenToday(todayStartEpoch).executeAsOne().toInt()
    }

    suspend fun getAverageBottleDuration(): Double = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.getAverageBottleDuration().executeAsOneOrNull()?.AVG ?: 0.0
    }

    suspend fun getTotalBottleDuration(): Double = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.getTotalBottleDuration().executeAsOneOrNull()?.SUM ?: 0.0
    }

    suspend fun getTotalBottles(): Int = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.getTotalBottles().executeAsOne().toInt()
    }

    suspend fun getAverageTimeBetweenBottles(): Double = withContext(Dispatchers.Default) {
        val feeds = getAllFeeds().sortedBy { it.start_time }
        if (feeds.size < 2) return@withContext 0.0
        
        var totalTimeBetween = 0.0
        var count = 0
        
        for (i in 1 until feeds.size) {
            val timeDiff = feeds[i].start_time - feeds[i - 1].start_time
            totalTimeBetween += timeDiff
            count++
        }
        
        if (count > 0) totalTimeBetween / count else 0.0
    }
} 