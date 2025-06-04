package com.teksta.projectparent.android // Assuming this is your MainActivity's package

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface // Make sure this is androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.russhwolf.settings.Settings
import com.teksta.projectparent.home.HomeScreen
import com.teksta.projectparent.HomeViewModel
import com.teksta.projectparent.bottles.BottlesScreen
import com.teksta.projectparent.models.BottlesViewModel
import com.teksta.projectparent.navigation.AppRoutes
import com.teksta.projectparent.onboarding.*
import com.teksta.projectparent.services.AndroidAppContext // Your object for application context
import com.teksta.projectparent.services.BottleFeedTrackerManager
import com.teksta.projectparent.services.BottleNotificationController
import com.teksta.projectparent.services.InAppReviewManager
import com.teksta.projectparent.services.ScreenIdleManager
import com.teksta.projectparent.services.getPlatformContext // This returns Application context
//import com.teksta.projectparent.ui.theme.ProjectParentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Initialize AndroidAppContext with the Application context
        AndroidAppContext.applicationContext = this.applicationContext

        val onboardingViewModel = OnboardingViewModel()
        val babyProfileViewModel = BabyProfileViewModel()
        val cardSelectionViewModel = CardSelectionViewModel()
        val homeViewModel = HomeViewModel()
        val bottleNotificationController = BottleNotificationController(getPlatformContext())

        // For managers that specifically need an Activity, pass 'this' (the MainActivity instance)
        // Ensure their 'expect constructor' takes 'Any' and 'actual constructor' casts to 'Activity'
        val inAppReviewManager = InAppReviewManager(this) // <<< PASS Activity (this)
        val screenIdleManager = ScreenIdleManager(this)   // <<< PASS Activity (this)

        // BottleFeedTrackerManager can often work with Application context
        val bottleFeedTrackerManager = BottleFeedTrackerManager(getPlatformContext())


        val bottlesViewModel = BottlesViewModel(
            settings = Settings(),
            bottleNotificationController = bottleNotificationController,
            bottleFeedTrackerManager = bottleFeedTrackerManager,
            inAppReviewManager = inAppReviewManager,
            screenIdleManager = screenIdleManager
        )

        setContent {
            ProjectParentTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        onboardingViewModel = onboardingViewModel,
                        babyProfileViewModel = babyProfileViewModel,
                        cardSelectionViewModel = cardSelectionViewModel,
                        homeViewModel = homeViewModel,
                        bottlesViewModel = bottlesViewModel
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
    homeViewModel: HomeViewModel,
    bottlesViewModel: BottlesViewModel
) {
    val navController = rememberNavController()

    val startDestination = if (Settings().getBoolean("showWelcomeOnboarding", true)) {
        AppRoutes.ONBOARDING_INITIAL
    } else {
        AppRoutes.HOME
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(AppRoutes.ONBOARDING_INITIAL) {
            InitialOnboardingScreen(
                viewModel = onboardingViewModel,
                navigateToBabyProfile = { navController.navigate(AppRoutes.ONBOARDING_BABY_PROFILE) },
                navigateToCardSelection = { navController.navigate(AppRoutes.ONBOARDING_CARD_SELECTION) },
                navigateToHome = {
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
        composable(AppRoutes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onNavigate = { routeString ->
                    val route = when (routeString) {
                        "PROFILE" -> AppRoutes.PROFILE
                        "BOTTLES" -> AppRoutes.BOTTLES
                        //"SLEEP" -> AppRoutes.SLEEP
                        "SETTINGS" -> AppRoutes.SETTINGS // Assuming you add this route
                        else -> return@HomeScreen
                    }
                    navController.navigate(route)
                },
                onNavigateToCharts = { navController.navigate(AppRoutes.CHARTS) }
            )
        }
        composable(AppRoutes.BOTTLES) {
            BottlesScreen(
                viewModel = bottlesViewModel,
                onNavigateToBottleList = { navController.navigate(AppRoutes.BOTTLE_LIST) }
            )
        }
        // Define other composable routes (PROFILE, BOTTLE_LIST, CHARTS, SETTINGS etc.)
        composable(AppRoutes.PROFILE) { Text("Profile Screen (To be implemented)")}
        composable(AppRoutes.BOTTLE_LIST) { Text("Bottle List Screen (To be implemented)")}
        composable(AppRoutes.CHARTS) { Text("Charts Screen (To be implemented)")}
        composable(AppRoutes.SETTINGS) { Text("Settings Screen (To be implemented)")}
    }
}