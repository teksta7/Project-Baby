package com.teksta.projectparent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.activity.compose.BackHandler

import projectparent.composeapp.generated.resources.Res
import projectparent.composeapp.generated.resources.compose_multiplatform
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private enum class OnboardingScreen {
    Initial, BabyProfile, CardSelection
}

private enum class AppScreen {
    Onboarding, Home, Profile, Bottles, Sleep, Food, Meds, Wind, Poo, Settings, Test, Charts
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
                var currentOnboardingScreen by remember { mutableStateOf(
                    when {
                        !onboardingComplete && OnboardingState.isBabyProfileComplete -> OnboardingScreen.Initial
                        !onboardingComplete && OnboardingState.isCardSelectionComplete -> OnboardingScreen.Initial
                        !onboardingComplete -> OnboardingScreen.Initial
                        else -> null
                    }
                ) }
                var currentAppScreen by remember { mutableStateOf(
                    if (onboardingComplete) AppScreen.Home else AppScreen.Onboarding
                ) }

                // Handle system back/gesture: return to Home if not on Home
                if (onboardingComplete && currentAppScreen != AppScreen.Home) {
                    BackHandler {
                        currentAppScreen = AppScreen.Home
                    }
                }

                if (!onboardingComplete && currentOnboardingScreen != null) {
                    AnimatedContent(
                        targetState = currentOnboardingScreen,
                        transitionSpec = {
                            // Slide animation from right to left when entering, left to right when exiting
                            slideInHorizontally(
                                animationSpec = tween(300, easing = EaseInOutCubic),
                                initialOffsetX = { fullWidth -> fullWidth }
                            ) + fadeIn(
                                animationSpec = tween(300)
                            ) togetherWith slideOutHorizontally(
                                animationSpec = tween(300, easing = EaseInOutCubic),
                                targetOffsetX = { fullWidth -> -fullWidth }
                            ) + fadeOut(
                                animationSpec = tween(300)
                            )
                        }
                    ) { screen ->
                        when (screen) {
                            OnboardingScreen.Initial -> InitialOnboardingScreen(
                                showWelcomeOnboarding = true,
                                isBabyProfileComplete = babyProfileComplete,
                                isCardSelectionComplete = cardSelectionComplete,
                                onShowBabyProfile = { currentOnboardingScreen = OnboardingScreen.BabyProfile },
                                onShowCardSelection = { currentOnboardingScreen = OnboardingScreen.CardSelection },
                                onFinish = {
                                    if (babyProfileComplete && cardSelectionComplete) {
                                        OnboardingState.isOnboardingComplete = true
                                        onboardingComplete = true
                                        currentAppScreen = AppScreen.Home
                                    }
                                }
                            )
                            OnboardingScreen.BabyProfile -> BabyProfileOnboardingScreen(
                                onComplete = { name, gender, birthDate, weight, profileImageUri ->
                                    // Save baby profile info as needed
                                    OnboardingState.isBabyProfileComplete = true
                                    babyProfileComplete = true
                                    currentOnboardingScreen = OnboardingScreen.Initial
                                },
                                onBack = { currentOnboardingScreen = OnboardingScreen.Initial }
                            )
                            OnboardingScreen.CardSelection -> CardSelectionOnboardingScreen(
                                onComplete = { selection ->
                                    // Save card selection as needed
                                    OnboardingState.isCardSelectionComplete = true
                                    cardSelectionComplete = true
                                    currentOnboardingScreen = OnboardingScreen.Initial
                                },
                                onBack = { currentOnboardingScreen = OnboardingScreen.Initial }
                            )
                            else -> null
                        }
                    }
                } else if (onboardingComplete) {
                    // Main App Navigation
                    AnimatedContent(
                        targetState = currentAppScreen,
                        transitionSpec = {
                            slideInHorizontally(
                                animationSpec = tween(300, easing = EaseInOutCubic),
                                initialOffsetX = { fullWidth -> fullWidth }
                            ) + fadeIn(
                                animationSpec = tween(300)
                            ) togetherWith slideOutHorizontally(
                                animationSpec = tween(300, easing = EaseInOutCubic),
                                targetOffsetX = { fullWidth -> -fullWidth }
                            ) + fadeOut(
                                animationSpec = tween(300)
                            )
                        }
                    ) { screen ->
                        when (screen) {
                            AppScreen.Home -> HomeView(
                                onNavigateToSection = { section ->
                                    currentAppScreen = when (section) {
                                        "PROFILE" -> AppScreen.Profile
                                        "BOTTLES" -> AppScreen.Bottles
                                        "SLEEP" -> AppScreen.Sleep
                                        "FOOD" -> AppScreen.Food
                                        "MEDS" -> AppScreen.Meds
                                        "WIND" -> AppScreen.Wind
                                        "POO" -> AppScreen.Poo
                                        "SETTINGS" -> AppScreen.Settings
                                        "TEST" -> AppScreen.Test
                                        else -> AppScreen.Home
                                    }
                                },
                                onShowCharts = {
                                    currentAppScreen = AppScreen.Charts
                                }
                            )
                            AppScreen.Profile -> ProfileScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Bottles -> BottlesScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Sleep -> SleepScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Food -> FoodScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Meds -> MedsScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Wind -> WindScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Poo -> PooScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Settings -> SettingsScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Test -> TestScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            AppScreen.Charts -> ChartsScreen(
                                onBack = { currentAppScreen = AppScreen.Home }
                            )
                            else -> HomeView(
                                onNavigateToSection = { section ->
                                    currentAppScreen = when (section) {
                                        "PROFILE" -> AppScreen.Profile
                                        "BOTTLES" -> AppScreen.Bottles
                                        "SLEEP" -> AppScreen.Sleep
                                        "FOOD" -> AppScreen.Food
                                        "MEDS" -> AppScreen.Meds
                                        "WIND" -> AppScreen.Wind
                                        "POO" -> AppScreen.Poo
                                        "SETTINGS" -> AppScreen.Settings
                                        "TEST" -> AppScreen.Test
                                        else -> AppScreen.Home
                                    }
                                },
                                onShowCharts = {
                                    currentAppScreen = AppScreen.Charts
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Placeholder screen composables - these will be implemented as you continue the migration
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Profile Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun BottlesScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Bottles Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun SleepScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sleep Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun FoodScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Food Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun MedsScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Medicine Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun WindScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Wind Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun PooScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Nappies Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Settings Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun TestScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Test Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}

@Composable
fun ChartsScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Charts Screen", color = Color.White)
            Button(onClick = onBack) {
                Text("Back to Home", color = Color.White)
            }
        }
    }
}