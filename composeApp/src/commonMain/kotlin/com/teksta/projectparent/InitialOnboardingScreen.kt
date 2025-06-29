package com.teksta.projectparent

//import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    
    // Animation states
    var logoVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    var cardsVisible by remember { mutableStateOf(false) }
    var finishButtonVisible by remember { mutableStateOf(false) }
    
    // Coroutine scope for animations
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        logoVisible = true
        delay(200)
        titleVisible = true
        delay(200)
        subtitleVisible = true
        delay(200)
        cardsVisible = true
        delay(200)
        finishButtonVisible = true
    }

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
                // Logo with fade and scale animation
                AnimatedVisibility(
                    visible = logoVisible,
                    enter = fadeIn(
                        animationSpec = tween(600, easing = EaseOutCubic)
                    ) + scaleIn(
                        animationSpec = tween(600, easing = EaseOutCubic),
                        initialScale = 0.8f
                    )
                ) {
                    Image(
                        painter = appLogoPainter(),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(240.dp).padding(bottom = 24.dp)
                    )
                }
                
                // Title with slide and fade animation
                AnimatedVisibility(
                    visible = titleVisible,
                    enter = fadeIn(
                        animationSpec = tween(500, easing = EaseOutCubic)
                    ) + slideInVertically(
                        animationSpec = tween(500, easing = EaseOutCubic),
                        initialOffsetY = { -it / 2 }
                    )
                ) {
                    Text(
                        "Welcome to Project Parent", 
                        fontSize = 28.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White, 
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Subtitle with slide and fade animation
                AnimatedVisibility(
                    visible = subtitleVisible,
                    enter = fadeIn(
                        animationSpec = tween(500, easing = EaseOutCubic)
                    ) + slideInVertically(
                        animationSpec = tween(500, easing = EaseOutCubic),
                        initialOffsetY = { -it / 2 }
                    )
                ) {
                    Text(
                        "Let's get started by completing the sections below to setup the app...", 
                        fontSize = 18.sp, 
                        color = Color.White, 
                        modifier = Modifier.padding(bottom = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
                
                // Cards with staggered animation
                AnimatedVisibility(
                    visible = cardsVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { it }
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Baby card with color animation
                        val babyCardColorAnimated by animateColorAsState(
                            targetValue = babyCardColor,
                            animationSpec = tween(500, easing = EaseInOutCubic),
                            label = "babyCardColor"
                        )
                        
                        var babyCardScale by remember { mutableStateOf(1f) }
                        
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(babyCardScale)
                                .background(babyCardColorAnimated, RoundedCornerShape(16.dp))
                                .clickable { 
                                    onShowBabyProfile()
                                    // Add scale animation on click
                                    babyCardScale = 0.95f
                                    coroutineScope.launch {
                                        delay(100)
                                        babyCardScale = 1f
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Baby", color = Color.White, fontSize = 20.sp)
                        }
                        
                        // Cards card with color animation
                        val cardsCardColorAnimated by animateColorAsState(
                            targetValue = cardsCardColor,
                            animationSpec = tween(500, easing = EaseInOutCubic),
                            label = "cardsCardColor"
                        )
                        
                        var cardsCardScale by remember { mutableStateOf(1f) }
                        
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(cardsCardScale)
                                .background(cardsCardColorAnimated, RoundedCornerShape(16.dp))
                                .clickable { 
                                    onShowCardSelection()
                                    // Add scale animation on click
                                    cardsCardScale = 0.95f
                                    coroutineScope.launch {
                                        delay(100)
                                        cardsCardScale = 1f
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Cards", color = Color.White, fontSize = 20.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Finish button with animation
                AnimatedVisibility(
                    visible = finishButtonVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInVertically(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetY = { it / 2 }
                    )
                ) {
                    val finishColorAnimated by animateColorAsState(
                        targetValue = finishColor,
                        animationSpec = tween(500, easing = EaseInOutCubic),
                        label = "finishColor"
                    )
                    
                    var finishButtonScale by remember { mutableStateOf(1f) }
                    
                    Box(
                        modifier = Modifier
                            .width(240.dp)
                            .height(60.dp)
                            .scale(finishButtonScale)
                            .background(finishColorAnimated, RoundedCornerShape(16.dp))
                            .clickable {
                                if (isBabyProfileComplete && isCardSelectionComplete) {
                                    showSuccess = true
                                    onFinish()
                                } else {
                                    showWarning = true
                                }
                                // Add scale animation on click
                                finishButtonScale = 0.95f
                                coroutineScope.launch {
                                    delay(100)
                                    finishButtonScale = 1f
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Finish", color = Color.White, fontSize = 22.sp)
                    }
                }
                
                // Warning message with animation
                AnimatedVisibility(
                    visible = showWarning,
                    enter = fadeIn(
                        animationSpec = tween(300, easing = EaseOutCubic)
                    ) + slideInVertically(
                        animationSpec = tween(300, easing = EaseOutCubic),
                        initialOffsetY = { -it / 2 }
                    ),
                    exit = fadeOut(
                        animationSpec = tween(300)
                    ) + slideOutVertically(
                        animationSpec = tween(300),
                        targetOffsetY = { -it / 2 }
                    )
                ) {
                    Text(
                        "One or more sections are incomplete", 
                        color = Color.Yellow, 
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                
                // Success message with animation
                AnimatedVisibility(
                    visible = showSuccess,
                    enter = fadeIn(
                        animationSpec = tween(300, easing = EaseOutCubic)
                    ) + slideInVertically(
                        animationSpec = tween(300, easing = EaseOutCubic),
                        initialOffsetY = { -it / 2 }
                    ),
                    exit = fadeOut(
                        animationSpec = tween(300)
                    ) + slideOutVertically(
                        animationSpec = tween(300),
                        targetOffsetY = { -it / 2 }
                    )
                ) {
                    Text(
                        "App is now ready to use", 
                        color = Color.Green, 
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                
                LaunchedEffect(showWarning) {
                    if (showWarning) {
                        delay(1500)
                        showWarning = false
                    }
                }
                
                LaunchedEffect(showSuccess) {
                    if (showSuccess) {
                        delay(1500)
                        showSuccess = false
                    }
                }
            }
        }
    }
} 