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
import com.russhwolf.settings.Settings
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import android.content.Intent

import projectparent.composeapp.generated.resources.Res
import projectparent.composeapp.generated.resources.compose_multiplatform
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.teksta.projectparent.db.BottleFeedRepository

private enum class OnboardingScreen {
    Initial, BabyProfile, CardSelection
}

enum class AppScreen {
    Onboarding, Home, Profile, Bottles, Sleep, Food, Meds, Wind, Poo, Settings, Test, Charts
}

@Composable
@Preview
fun App(
    bottleFeedViewModel: BottleFeedViewModel? = null,
    navInterceptor: ((AppScreen) -> Unit)? = null
) {
    val settings = remember { Settings() }
    val context = LocalContext.current
    var initialScreen: AppScreen by remember { mutableStateOf(AppScreen.Home) }
    LaunchedEffect(Unit) {
        if (context is Activity) {
            val intent = context.intent
            val navTo = intent.getStringExtra("navigateTo")
            if (navTo == "BOTTLE_FEED") {
                initialScreen = AppScreen.Bottles
            } else {
                // Restore last screen from settings
                val last = settings.getStringOrNull("last_screen")
                initialScreen = last?.let { AppScreen.valueOf(it) } ?: AppScreen.Home
            }
        }
    }
    var currentAppScreen by remember { mutableStateOf(initialScreen) }
    fun navigateTo(screen: AppScreen) {
        currentAppScreen = screen
        settings.putString("last_screen", screen.name)
        navInterceptor?.invoke(screen)
    }
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

                // Use the provided bottleFeedViewModel if available, otherwise create a mock for preview
                val actualBottleFeedViewModel = bottleFeedViewModel ?: remember { null as BottleFeedViewModel? }

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
                                        navigateTo(AppScreen.Home)
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
                                bottleFeedViewModel = actualBottleFeedViewModel,
                                onNavigateToSection = { section ->
                                    navigateTo(when (section) {
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
                                    })
                                },
                                onShowCharts = {
                                    navigateTo(AppScreen.Charts)
                                }
                            )
                            AppScreen.Profile -> ProfileScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            AppScreen.Bottles -> {
                                if (actualBottleFeedViewModel != null) {
                                    BottleFeedScreen(
                                        viewModel = actualBottleFeedViewModel,
                                        onBack = { navigateTo(AppScreen.Home) }
                                    )
                                } else {
                                    // Fallback for preview
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Bottle Feed Screen", color = Color.White)
                                            Button(onClick = { navigateTo(AppScreen.Home) }) {
                                                Text("Back to Home", color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                            AppScreen.Sleep -> SleepScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            AppScreen.Food -> FoodScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            AppScreen.Meds -> MedsScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            AppScreen.Wind -> WindScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            AppScreen.Poo -> PooScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            AppScreen.Settings -> SettingsScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            AppScreen.Test -> TestScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            AppScreen.Charts -> ChartsScreen(
                                onBack = { navigateTo(AppScreen.Home) }
                            )
                            else -> HomeView(
                                bottleFeedViewModel = actualBottleFeedViewModel,
                                onNavigateToSection = { section ->
                                    navigateTo(when (section) {
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
                                    })
                                },
                                onShowCharts = {
                                    navigateTo(AppScreen.Charts)
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