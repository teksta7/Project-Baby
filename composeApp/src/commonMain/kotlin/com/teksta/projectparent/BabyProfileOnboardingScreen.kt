package com.teksta.projectparent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.draw.scale
import androidx.activity.compose.BackHandler
import kotlinx.datetime.LocalDateTime
import com.teksta.projectparent.DatePickerField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.text.style.LocalTextStyle

// Lottie imports (Android only)
import com.airbnb.lottie.compose.*
import java.io.File
import android.graphics.BitmapFactory
import android.net.Uri
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.teksta.projectparent.ProfileImagePicker

@Composable
fun BabyProfileOnboardingScreen(
    onComplete: (name: String, gender: String, birthDate: String, weight: String, profileImageUri: String?) -> Unit,
    onBack: () -> Unit
) {
    // Back gesture handler
    BackHandler {
        onBack()
    }
    
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Boy") }
    var birthDate by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf(0) }
    var weightOz by remember { mutableStateOf(0) }
    var showError by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<String?>(null) }
    
    // Animation states for staggered entrance
    var backButtonVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var nameFieldVisible by remember { mutableStateOf(false) }
    var genderFieldVisible by remember { mutableStateOf(false) }
    var weightFieldVisible by remember { mutableStateOf(false) }
    var dateFieldVisible by remember { mutableStateOf(false) }
    var imagePickerVisible by remember { mutableStateOf(false) }
    var completeButtonVisible by remember { mutableStateOf(false) }

    // Lottie animation setup (Android only)
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("Animation.json"))
    val progress by animateLottieCompositionAsState(composition)
    
    // Coroutine scope for animations
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        backButtonVisible = true
        delay(100)
        titleVisible = true
        delay(150)
        nameFieldVisible = true
        delay(150)
        genderFieldVisible = true
        delay(150)
        weightFieldVisible = true
        delay(150)
        dateFieldVisible = true
        delay(150)
        imagePickerVisible = true
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
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie animation with fade and scale animation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(600, easing = EaseOutCubic)
                    ) + scaleIn(
                        animationSpec = tween(600, easing = EaseOutCubic),
                        initialScale = 0.8f
                    )
                ) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(180.dp)
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
                        "Create a Baby Profile", 
                        fontSize = 24.sp, 
                        color = Color.White, 
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                
                // Name field with slide animation
                AnimatedVisibility(
                    visible = nameFieldVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { it }
                    )
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Baby's Name", color = Color.White) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }
                
                // Gender field with slide animation
                AnimatedVisibility(
                    visible = genderFieldVisible,
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
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Gender:", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                        DropdownMenuBox(selected = gender, options = listOf("Boy", "Girl")) { gender = it }
                    }
                }
                
                // Weight field with slide animation
                AnimatedVisibility(
                    visible = weightFieldVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInVertically(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetY = { it / 2 }
                    )
                ) {
                    Column {
                        // Birth weight label above steppers
                        Text(
                            "Birth Weight:",
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Lbs stepper
                            Stepper(
                                value = weightLbs,
                                onValueChange = { weightLbs = it.coerceAtLeast(0) },
                                label = "lbs"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            // Oz stepper
                            Stepper(
                                value = weightOz,
                                onValueChange = { weightOz = it.coerceIn(0, 15) },
                                label = "oz"
                            )
                        }
                    }
                }
                
                // Date field with slide animation
                AnimatedVisibility(
                    visible = dateFieldVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { it }
                    )
                ) {
                    DatePickerField(
                        label = "Select Birth Date",
                        date = birthDate,
                        onDateSelected = { birthDate = it }
                    )
                }
                
                // Image picker with slide animation
                AnimatedVisibility(
                    visible = imagePickerVisible,
                    enter = fadeIn(
                        animationSpec = tween(400, easing = EaseOutCubic)
                    ) + slideInHorizontally(
                        animationSpec = tween(400, easing = EaseOutCubic),
                        initialOffsetX = { -it }
                    )
                ) {
                    // Profile picture picker (expect/actual)
                    ProfileImagePicker(
                        imageUri = profileImageUri,
                        onImageSelected = { profileImageUri = it }
                    )
                }
                
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
                            if (name.isNotBlank() && birthDate.isNotBlank()) {
                                val weight = "${weightLbs} lbs ${weightOz} oz"
                                onComplete(name, gender, birthDate, weight, profileImageUri)
                            } else {
                                showError = true
                            }
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
                        Text("Complete baby profile", color = Color.White)
                    }
                }
                
                // Error message with animation
                AnimatedVisibility(
                    visible = showError,
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
                        "Please fill all required fields.", 
                        color = MaterialTheme.colorScheme.error, 
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                
                LaunchedEffect(showError) {
                    if (showError) {
                        delay(1500)
                        showError = false
                    }
                }
            }
        }
    }
}

@Composable
fun DropdownMenuBox(selected: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun Stepper(value: Int, onValueChange: (Int) -> Unit, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { onValueChange(value - 1) }, enabled = value > 0) { Text("-") }
        Text(" $value $label ", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
        Button(onClick = { onValueChange(value + 1) }) { Text("+") }
    }
} 