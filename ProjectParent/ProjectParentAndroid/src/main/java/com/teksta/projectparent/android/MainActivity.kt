package com.teksta.projectparent.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.russhwolf.settings.Settings
import com.teksta.projectparent.HomeViewModel
import com.teksta.projectparent.home.HomeScreen
import com.teksta.projectparent.navigation.AppRoutes
import com.teksta.projectparent.onboarding.*
import com.teksta.projectparent.android.ProjectParentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This enables edge-to-edge drawing. It's important for ensuring
        // status bar padding modifiers work correctly.
       // WindowCompat.setDecorFitsSystemWindows(window, false)

        // In a production app, you would use a dependency injection library like Koin or Hilt
        // to provide these ViewModels instead of creating them directly here.
        val onboardingViewModel = OnboardingViewModel()
        val babyProfileViewModel = BabyProfileViewModel()
        val cardSelectionViewModel = CardSelectionViewModel()
        val homeViewModel = HomeViewModel()

        setContent {
            ProjectParentTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        onboardingViewModel = onboardingViewModel,
                        babyProfileViewModel = babyProfileViewModel,
                        cardSelectionViewModel = cardSelectionViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    onboardingViewModel: OnboardingViewModel,
    babyProfileViewModel: BabyProfileViewModel,
    cardSelectionViewModel: CardSelectionViewModel,
    homeViewModel: HomeViewModel
) {
    val navController = rememberNavController()

    // Decide the starting screen based on whether onboarding is complete
    val startDestination = if (Settings().getBoolean("showWelcomeOnboarding", true)) {
        AppRoutes.ONBOARDING_INITIAL
    } else {
        AppRoutes.HOME
    }

    NavHost(navController = navController, startDestination = startDestination) {
        // --- Onboarding Graph ---
        composable(AppRoutes.ONBOARDING_INITIAL) {
            InitialOnboardingScreen(
                viewModel = onboardingViewModel,
                navigateToBabyProfile = { navController.navigate(AppRoutes.ONBOARDING_BABY_PROFILE) },
                navigateToCardSelection = { navController.navigate(AppRoutes.ONBOARDING_CARD_SELECTION) },
                navigateToHome = {
                    // Navigate to home and clear all onboarding screens from the back stack
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.ONBOARDING_INITIAL) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoutes.ONBOARDING_BABY_PROFILE) {
            BabyProfileOnboardingScreen(
                viewModel = babyProfileViewModel,
                onDismissRequest = { navController.popBackStack() }
            )
        }
        composable(AppRoutes.ONBOARDING_CARD_SELECTION) {
            CardSelectionOnboardingScreen(
                viewModel = cardSelectionViewModel,
                onDismissRequest = { navController.popBackStack() }
            )
        }

        // --- Main App Graph ---
        composable(AppRoutes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigate = { route -> navController.navigate(route) },
                onNavigateToCharts = { navController.navigate(AppRoutes.CHARTS) }
            )
        }

        // Add placeholders for the other main app screens
        composable(AppRoutes.PROFILE) {
            // ProfileScreen(...)
        }
        composable(AppRoutes.BOTTLES) {
            // BottlesScreen(...)
        }
        composable(AppRoutes.CHARTS) {
            // BottleChartsScreen(...)
        }
        // ... and so on
    }
}