package com.teksta.projectparent.onboarding

// In androidMain/com/projectparent/onboarding/InitialOnboardingScreen.kt

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.teksta.projectparent.R
import org.jetbrains.annotations.Debug

// Your generated resource class

@Composable
fun InitialOnboardingScreen(
    viewModel: OnboardingViewModel,
    navigateToBabyProfile: () -> Unit, // Callback to navigate to the Baby Profile screen
    navigateToCardSelection:() -> Unit,  //Callback to navigate to the card selection screen
    navigateToHome: () -> Unit // Callback to navigate to the Home screen
) {
    val uiState by viewModel.uiState.collectAsState()

    if (!uiState.showWelcomeOnboarding) {
    // if (uiState.showWelcomeOnboarding) {
        // If onboarding is complete, navigate to the home screen.
        // This is a common pattern in Compose navigation.
        LaunchedEffect(Unit) {
            navigateToHome()
        }
        return
    }

    //Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            Image(
//                painter = painterResource(id = R.drawable.app_logo), // Your logo from drawable resources
//                contentDescription = "App Logo",
//                modifier = Modifier.size(200.dp)
//            )

            Text(
                text = "Welcome to Project Parent",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(250.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Lets get started by completing the sections below to setup the app...",
                fontSize = 18.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(300.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Row {
                OnboardingActionCard(
                    text = "Baby",
                    isComplete = uiState.isBabyProfileOnboardingComplete,
                    //onClick = viewModel::onBabyProfileClicked
                    onClick = navigateToBabyProfile //Call the navigation function
                )
                Spacer(modifier = Modifier.width(24.dp))
                OnboardingActionCard(
                    text = "Cards",
                    isComplete = uiState.isCardSelectionOnboardingComplete,
                    //onClick = viewModel::onCardSelectionClicked
                    onClick = navigateToCardSelection
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::onFinishClicked,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (uiState.isBabyProfileOnboardingComplete && uiState.isCardSelectionOnboardingComplete) Color.Green else Color.DarkGray
                ),
                modifier = Modifier.size(width = 240.dp, height = 60.dp)
            ) {
                Text("Finish", color = Color.White)
            }
        }
    }

    // --- Dialogs and Sheets ---

    if (uiState.showBabyProfileSheet) {
        // In a real app, this would be a full-screen dialog or a new screen
        AlertDialog(
            onDismissRequest = viewModel::onBabyProfileOnboardingDismiss,
            title = { Text("Baby Profile Onboarding") },
            text = { Text("This is where the BabyProfileOnboardingView would be.") },
            confirmButton = { Button(onClick = viewModel::onBabyProfileOnboardingDismiss) { Text("Done") } }
        )
    }

    if (uiState.showCardSelectionSheet) {
        AlertDialog(
            onDismissRequest = viewModel::onCardSelectionOnboardingDismiss,
            title = { Text("Card Selection Onboarding") },
            text = { Text("This is where the CardSelectionOnboardingView would be.") },
            confirmButton = { Button(onClick = viewModel::onCardSelectionOnboardingDismiss) { Text("Done") } }
        )
    }

    uiState.showAlert?.let { alertInfo ->
        AlertDialog(
            onDismissRequest = viewModel::onAlertDismissed,
            title = { Text(alertInfo.title) },
            text = { Text(alertInfo.message) },
            confirmButton = { Button(onClick = viewModel::onAlertDismissed) { Text("OK") } }
        )
    }
}

@Composable
private fun OnboardingActionCard(text: String, isComplete: Boolean, onClick: () -> Unit) {
    val color by animateColorAsState(if (isComplete) Color.Green else Color(0xFFF0E68C)) // Khaki color

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.5f),
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = Color.White)
        }
    }
}