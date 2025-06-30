package com.teksta.projectparent

import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.teksta.projectparent.db.AppDatabase
import com.teksta.projectparent.db.AppDatabaseWrapper
import com.teksta.projectparent.db.BottleFeedRepository
import android.util.Log

@Composable
fun App() {
    val context = LocalContext.current
    val database = remember { AppDatabaseWrapper(context) }
    val repository = remember { BottleFeedRepository(database) }
    val bottleFeedViewModel = remember { BottleFeedViewModel(repository) }
    
    App(bottleFeedViewModel)
}

actual fun logDebug(tag: String, message: String) {
    Log.d(tag, message)
} 