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

class MainActivity : ComponentActivity() {
    companion object {
        var instance: MainActivity? = null
            private set
        // Used for Compose navigation from notification clicks
        var navTarget: androidx.compose.runtime.MutableState<String?> = mutableStateOf(null)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Initialize onboarding prefs for persistence
        OnboardingState.init(OnboardingPrefs(this))

        setContent {
            AppAndroid()
        }
    }
    override fun onDestroy() {
        if (instance === this) {
            instance = null
        }
        super.onDestroy()
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
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