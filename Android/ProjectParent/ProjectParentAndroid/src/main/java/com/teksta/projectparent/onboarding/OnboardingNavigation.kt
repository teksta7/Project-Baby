package com.teksta.projectparent.onboarding

// In androidApp/src/main/java/com/teksta/projectparent/onboarding/OnboardingNavigation.kt

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * This object holds the unique string identifiers for your navigation routes.
 * Keeping them here makes them reusable and prevents errors from typos.
 */
object OnboardingRoutes {
    const val INITIAL_SCREEN = "initial_onboarding"
    const val BABY_PROFILE_SCREEN = "baby_profile_onboarding"
    const val CARD_SELECTION_SCREEN = "card_selection_onboarding"
    // Add other routes here as your app grows
}

@Composable
fun OnboardingNavigation(
    onboardingViewModel: OnboardingViewModel,
    babyProfileViewModel: BabyProfileViewModel,
    cardSelectionViewModel: CardSelectionViewModel, // Add new ViewModel

    navigateToHome: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = OnboardingRoutes.INITIAL_SCREEN // Use the constant here
    ) {
        // Define the first screen in the navigation graph
        composable(OnboardingRoutes.INITIAL_SCREEN) { // And here
            InitialOnboardingScreen(
                viewModel = onboardingViewModel,
                navigateToBabyProfile = {
                    navController.navigate(OnboardingRoutes.BABY_PROFILE_SCREEN) // And here
                },
                navigateToCardSelection = {
                    navController.navigate(OnboardingRoutes.CARD_SELECTION_SCREEN) // And here
                },
                navigateToHome = navigateToHome
            )
        }
        // Define the second screen (the full-screen "pop-up")
        composable(OnboardingRoutes.BABY_PROFILE_SCREEN) { // And here
            BabyProfileOnboardingScreen(
                viewModel = babyProfileViewModel,
                onDismissRequest = {
                    navController.popBackStack()
                }
            )
        }

        // New screen entry
        composable(OnboardingRoutes.CARD_SELECTION_SCREEN) {
            CardSelectionOnboardingScreen(
                viewModel = cardSelectionViewModel,
                onDismissRequest = { navController.popBackStack() }
            )
        }
    }
}