package com.teksta.projectparent.db

import com.teksta.projectparent.db.AppDatabase
import com.teksta.projectparent.db.BottleFeed
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BottleFeedRepository(private val database: AppDatabase) {
    suspend fun getAllFeeds(): List<BottleFeed> = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.selectAll().executeAsList()
    }

    suspend fun insertFeed(amount: Double, timestamp: Long, note: String?) = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.insertFeed(amount, timestamp, note)
    }

    suspend fun updateFeed(id: Long, amount: Double, timestamp: Long, note: String?) = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.updateFeed(amount, timestamp, note, id)
    }

    suspend fun deleteFeed(id: Long) = withContext(Dispatchers.Default) {
        database.bottleFeedQueries.deleteFeed(id)
    }
} 