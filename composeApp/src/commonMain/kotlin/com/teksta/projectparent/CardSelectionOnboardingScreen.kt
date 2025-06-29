package com.teksta.projectparent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.scale
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun CardSelectionOnboardingScreen(
    initialSelection: CardSelectionState = CardSelectionState(),
    onComplete: (CardSelectionState) -> Unit,
    onBack: () -> Unit
) {
    // Back gesture handler
    BackHandler {
        onBack()
    }
    
    var bottles by remember { mutableStateOf(initialSelection.bottles) }
    var sleep by remember { mutableStateOf(initialSelection.sleep) }
    var food by remember { mutableStateOf(initialSelection.food) }
    var meds by remember { mutableStateOf(initialSelection.meds) }
    var wind by remember { mutableStateOf(initialSelection.wind) }
    var poo by remember { mutableStateOf(initialSelection.poo) }
    
    // Animation states for staggered entrance
    var backButtonVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var descriptionVisible by remember { mutableStateOf(false) }
    var bottlesVisible by remember { mutableStateOf(false) }
    var sleepVisible by remember { mutableStateOf(false) }
    var foodVisible by remember { mutableStateOf(false) }
    var medsVisible by remember { mutableStateOf(false) }
    var windVisible by remember { mutableStateOf(false) }
    var pooVisible by remember { mutableStateOf(false) }
    var completeButtonVisible by remember { mutableStateOf(false) }
    
    // Coroutine scope for animations
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        backButtonVisible = true
        delay(100)
        titleVisible = true
        delay(150)
        descriptionVisible = true
        delay(150)
        bottlesVisible = true
        delay(100)
        sleepVisible = true
        delay(100)
        foodVisible = true
        delay(100)
        medsVisible = true
        delay(100)
        windVisible = true
        delay(100)
        pooVisible = true
        delay(150)
        completeButtonVisible = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Back button with slide animation
        AnimatedVisibility(
            visible = backButtonVisible,
            enter = fadeIn(
                animationSpec = tween(400, easing = EaseOutCubic)
            ) + slideInHorizontally(
                animationSpec = tween(400, easing = EaseOutCubic),
                initialOffsetX = { -it }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 16.dp, start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "< Back",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable { onBack() }
                        .padding(8.dp)
                )
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        "Card Selection", 
                        fontSize = 24.sp, 
                        color = Color.White, 
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // Description with slide and fade animation
                AnimatedVisibility(
                    visible = descriptionVisible,
                    enter = fadeIn(
                        animationSpec = tween(500, easing = EaseOutCubic)
                    ) + slideInVertically(
                        animationSpec = tween(500, easing = EaseOutCubic),
                        initialOffsetY = { -it / 2 }
                    )
                ) {
                    Text(
                        "Select which baby stat cards to enable.", 
                        fontSize = 16.sp, 
                        color = Color.White, 
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                
                // Toggle rows with staggered slide animations
                AnimatedVisibility(
                    visible = bottlesVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { it }
                    )
                ) {
                    ToggleRow("Bottles", bottles, { bottles = it }, coroutineScope)
                }
                
                AnimatedVisibility(
                    visible = sleepVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { -it }
                    )
                ) {
                    ToggleRow("Sleep", sleep, { sleep = it }, coroutineScope)
                }
                
                AnimatedVisibility(
                    visible = foodVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { it }
                    )
                ) {
                    ToggleRow("Food", food, { food = it }, coroutineScope)
                }
                
                AnimatedVisibility(
                    visible = medsVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { -it }
                    )
                ) {
                    ToggleRow("Meds", meds, { meds = it }, coroutineScope)
                }
                
                AnimatedVisibility(
                    visible = windVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { it }
                    )
                ) {
                    ToggleRow("Wind", wind, { wind = it }, coroutineScope)
                }
                
                AnimatedVisibility(
                    visible = pooVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { -it }
                    )
                ) {
                    ToggleRow("Poo", poo, { poo = it }, coroutineScope)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Complete button with slide animation
                AnimatedVisibility(
                    visible = completeButtonVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInVertically(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetY = { it / 2 }
                    )
                ) {
                    var buttonScale by remember { mutableStateOf(1f) }
                    
                    Button(
                        onClick = { 
                            onComplete(CardSelectionState(bottles, sleep, food, meds, wind, poo))
                            // Add scale animation on click
                            buttonScale = 0.95f
                            coroutineScope.launch {
                                delay(100)
                                buttonScale = 1f
                            }
                        }, 
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(buttonScale)
                    ) {
                        Text("Complete card selection", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, coroutineScope: CoroutineScope) {
    var switchScale by remember { mutableStateOf(1f) }
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.scale(switchScale)
        ) {
            Switch(
                checked = checked, 
                onCheckedChange = { 
                    onCheckedChange(it)
                    // Add a subtle scale animation when toggled
                    switchScale = 1.1f
                    coroutineScope.launch {
                        delay(100)
                        switchScale = 1f
                    }
                }
            )
        }
    }
}

data class CardSelectionState(
    val bottles: Boolean = true,
    val sleep: Boolean = false,
    val food: Boolean = false,
    val meds: Boolean = false,
    val wind: Boolean = false,
    val poo: Boolean = false
) 