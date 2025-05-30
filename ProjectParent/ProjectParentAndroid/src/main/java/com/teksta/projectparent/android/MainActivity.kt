package com.teksta.projectparent.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.teksta.projectparent.Greeting
import com.teksta.projectparent.onboarding.InitialOnboardingScreen
import com.teksta.projectparent.onboarding.OnboardingViewModel // This is from your shared module
import com.teksta.projectparent.onboarding.BabyProfileViewModel
import com.teksta.projectparent.onboarding.CardSelectionViewModel
import com.teksta.projectparent.onboarding.OnboardingNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onboardingViewModel = OnboardingViewModel()
        val babyProfileViewModel = BabyProfileViewModel()
        val cardSelectionViewModel = CardSelectionViewModel()


        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    OnboardingNavigation(
                        onboardingViewModel = onboardingViewModel,
                        babyProfileViewModel = babyProfileViewModel,
                        cardSelectionViewModel = cardSelectionViewModel,
                        navigateToHome = {
                            // This is where you would navigate to your main app screen
                            // after onboarding is finished.
                            println("Navigate to Home Screen!")
                        }
                    )
//                    InitialOnboardingScreen(
//                        viewModel = onboardingViewModel,
//                        navigateToHome = { /* Handle navigation to home screen */ }
//                    )
                }

//                {
//                    GreetingView(Greeting().greet())
//                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!")
    }
}
