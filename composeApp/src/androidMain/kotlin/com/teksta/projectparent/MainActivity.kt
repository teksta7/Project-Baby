package com.teksta.projectparent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Initialize onboarding prefs for persistence
        OnboardingState.init(OnboardingPrefs(this))

        setContent {
            App(this)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    // For preview, use the common App without database
    App()
}