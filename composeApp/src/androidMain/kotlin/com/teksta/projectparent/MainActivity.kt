// MainActivity is the Android entry point for Project Baby. It sets up notification permissions, broadcast receivers, and Compose content.
package com.teksta.projectparent

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.SharedPreferences

class MainActivity : ComponentActivity() {
    companion object {
        var instance: MainActivity? = null
            private set
        // Used for Compose navigation from notification clicks
        var navTarget: androidx.compose.runtime.MutableState<String?> = mutableStateOf(null)
        // Reference to the current BottleFeedViewModel for broadcast sync
        var currentBottleFeedViewModel: BottleFeedViewModel? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Register broadcast receiver for bottle feed finish/cancel actions from notifications
        val filter = IntentFilter().apply {
            addAction("com.teksta.projectparent.BOTTLE_FEED_FINISHED")
            addAction("com.teksta.projectparent.BOTTLE_FEED_CANCELLED")
        }
        registerReceiver(
            object : BroadcastReceiver() {
                override fun onReceive(context: android.content.Context?, intent: Intent?) {
                    val action = intent?.action
                    android.util.Log.d("BottleFeedBroadcast", "Received broadcast: $action")
                    // Sync notification actions with the ViewModel
                    currentBottleFeedViewModel?.let {
                        when (action) {
                            "com.teksta.projectparent.BOTTLE_FEED_FINISHED" -> {
                                android.util.Log.d("BottleFeedBroadcast", "Calling stopTimer with saveFeed = true")
                                it.stopTimer(fromExternal = true, saveFeed = true)
                            }
                            "com.teksta.projectparent.BOTTLE_FEED_CANCELLED" -> {
                                android.util.Log.d("BottleFeedBroadcast", "Calling stopTimer with saveFeed = false")
                                it.stopTimer(fromExternal = true, saveFeed = false)
                            }
                        }
                    }
                }
            },
            filter,
            RECEIVER_NOT_EXPORTED
        )

        // Request notification permission on Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Initialize onboarding preferences for multiplatform onboarding state
        OnboardingState.init(OnboardingPrefs(this))

        // Register SharedPreferences listener for bottle_feed_action (syncs notification actions with ViewModel)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "bottle_feed_action") {
                val action = prefs.getString(key, "") ?: ""
                android.util.Log.d("BottleFeedPrefListener", "bottle_feed_action changed to $action")
                currentBottleFeedViewModel?.onBottleFeedActionChanged(action)
                prefs.edit().putString(key, "").apply()
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(prefListener)

        // Set up the main Compose UI
        setContent {
            AppAndroid()
        }
    }
    override fun onDestroy() {
        // Clear static instance reference on destroy
        if (instance === this) {
            instance = null
        }
        super.onDestroy()
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Used for navigation from notification clicks
        val navTo = intent.getStringExtra("navigateTo")
        navTarget.value = navTo
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // For preview, use the common App without database
    AppAndroid()
}