package com.teksta.projectparent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import projectparent.composeapp.generated.resources.Res
import projectparent.composeapp.generated.resources.compose_multiplatform
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private enum class OnboardingScreen {
    Initial, BabyProfile, CardSelection
}

@Composable
@Preview
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black)
            ) {
                var onboardingComplete by remember { mutableStateOf(OnboardingState.isOnboardingComplete) }
                var babyProfileComplete by remember { mutableStateOf(OnboardingState.isBabyProfileComplete) }
                var cardSelectionComplete by remember { mutableStateOf(OnboardingState.isCardSelectionComplete) }
                var currentScreen by remember { mutableStateOf(
                    when {
                        !onboardingComplete && OnboardingState.isBabyProfileComplete -> OnboardingScreen.Initial
                        !onboardingComplete && OnboardingState.isCardSelectionComplete -> OnboardingScreen.Initial
                        !onboardingComplete -> OnboardingScreen.Initial
                        else -> null
                    }
                ) }

                if (!onboardingComplete && currentScreen != null) {
                    when (currentScreen) {
                        OnboardingScreen.Initial -> InitialOnboardingScreen(
                            showWelcomeOnboarding = true,
                            isBabyProfileComplete = babyProfileComplete,
                            isCardSelectionComplete = cardSelectionComplete,
                            onShowBabyProfile = { currentScreen = OnboardingScreen.BabyProfile },
                            onShowCardSelection = { currentScreen = OnboardingScreen.CardSelection },
                            onFinish = {
                                if (babyProfileComplete && cardSelectionComplete) {
                                    OnboardingState.isOnboardingComplete = true
                                    onboardingComplete = true
                                }
                            }
                        )
                        OnboardingScreen.BabyProfile -> BabyProfileOnboardingScreen(
                            onComplete = { name, gender, birthDate, weight ->
                                // Save baby profile info as needed
                                OnboardingState.isBabyProfileComplete = true
                                babyProfileComplete = true
                                currentScreen = OnboardingScreen.Initial
                            },
                            onBack = { currentScreen = OnboardingScreen.Initial }
                        )
                        OnboardingScreen.CardSelection -> CardSelectionOnboardingScreen(
                            onComplete = { selection ->
                                // Save card selection as needed
                                OnboardingState.isCardSelectionComplete = true
                                cardSelectionComplete = true
                                currentScreen = OnboardingScreen.Initial
                            },
                            onBack = { currentScreen = OnboardingScreen.Initial }
                        )
                        else -> null
                    }
                } else if (onboardingComplete) {
                    // Main Home Screen (replace with your actual home screen)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Welcome to the Home Screen! Onboarding is complete.")
                    }
                }
            }
        }
    }
}