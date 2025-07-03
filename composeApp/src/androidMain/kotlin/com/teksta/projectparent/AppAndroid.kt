package com.teksta.projectparent

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.teksta.projectparent.db.AppDatabase
import com.teksta.projectparent.db.AppDatabaseWrapper
import com.teksta.projectparent.db.BottleFeedRepository

@Composable
fun AppAndroid(bottleFeedViewModel: BottleFeedViewModel? = null) {
    val context = LocalContext.current
    val database = remember(context) { AppDatabaseWrapper(context) }
    val repository = remember { BottleFeedRepository(database) }
    val actualBottleFeedViewModel = bottleFeedViewModel ?: remember { BottleFeedViewModel(repository) }
    
    var navigationFunction by remember { mutableStateOf<((AppScreen) -> Unit)?>(null) }
    
    App(
        bottleFeedViewModel = actualBottleFeedViewModel,
        navInterceptor = { screen ->
            // Call the navigation function if we have one
            navigationFunction?.invoke(screen)
        }
    )
    
    // Handle notification navigation
    val navTarget = MainActivity.navTarget.value
    LaunchedEffect(navTarget) {
        if (navTarget == "BOTTLE_FEED") {
            navigationFunction?.invoke(AppScreen.Bottles)
            MainActivity.navTarget.value = null
        }
    }
}
