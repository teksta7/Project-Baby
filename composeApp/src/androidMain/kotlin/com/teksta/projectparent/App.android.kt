package com.teksta.projectparent

import android.content.Context
import androidx.compose.runtime.*
import com.teksta.projectparent.db.AppDatabaseWrapper
import com.teksta.projectparent.db.BottleFeedRepository

@Composable
fun App(context: Context) {
    // Initialize database and bottle feed view model
    val database = remember { AppDatabaseWrapper(context) }
    val bottleFeedRepository = remember { BottleFeedRepository(database) }
    val bottleFeedViewModel = remember { BottleFeedViewModel(bottleFeedRepository) }
    
    // Call the common App composable with the initialized view model
    App(bottleFeedViewModel = bottleFeedViewModel)
} 