package com.teksta.projectparent

//import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InitialOnboardingScreen(
    showWelcomeOnboarding: Boolean,
    isBabyProfileComplete: Boolean,
    isCardSelectionComplete: Boolean,
    onShowBabyProfile: () -> Unit,
    onShowCardSelection: () -> Unit,
    onFinish: () -> Unit
) {
    val cardBackground = Color(0xFFFFC107) // Material Amber 500 for better readability

    var babyCardColor by remember { mutableStateOf(cardBackground) }
    var cardsCardColor by remember { mutableStateOf(cardBackground) }
    var finishColor by remember { mutableStateOf(Color.Red) }
    var showWarning by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(isBabyProfileComplete, isCardSelectionComplete) {
        babyCardColor = if (isBabyProfileComplete) Color(0xFF4CAF50) else cardBackground
        cardsCardColor = if (isCardSelectionComplete) Color(0xFF4CAF50) else cardBackground
        finishColor = if (isBabyProfileComplete && isCardSelectionComplete) Color(0xFF4CAF50) else Color.Red
    }

    if (showWelcomeOnboarding) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = appLogoPainter(),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(240.dp).padding(bottom = 24.dp)
                )
                Text(
                    "Welcome to Project Parent", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Let's get started by completing the sections below to setup the app...", fontSize = 18.sp, color = Color.White, modifier = Modifier.padding(bottom = 32.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(babyCardColor, RoundedCornerShape(16.dp))
                            .clickable { onShowBabyProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Baby", color = Color.White, fontSize = 20.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(cardsCardColor, RoundedCornerShape(16.dp))
                            .clickable { onShowCardSelection() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cards", color = Color.White, fontSize = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp)
                        .background(finishColor, RoundedCornerShape(16.dp))
                        .clickable {
                            if (isBabyProfileComplete && isCardSelectionComplete) {
                                showSuccess = true
                                onFinish()
                            } else {
                                showWarning = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Finish", color = Color.White, fontSize = 22.sp)
                }
                if (showWarning) {
                    Text("One or more sections are incomplete", color = Color.Yellow, modifier = Modifier.padding(top = 16.dp))
                    LaunchedEffect(showWarning) {
                        kotlinx.coroutines.delay(1500)
                        showWarning = false
                    }
                }
                if (showSuccess) {
                    Text("App is now ready to use", color = Color.Green, modifier = Modifier.padding(top = 16.dp))
                    LaunchedEffect(showSuccess) {
                        kotlinx.coroutines.delay(1500)
                        showSuccess = false
                    }
                }
            }
        }
    }
} 