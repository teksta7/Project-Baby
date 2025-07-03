package com.teksta.projectparent

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.activity.compose.BackHandler
import com.teksta.projectparent.LocalDismissNotifications
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.ui.draw.scale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun BottleFeedScreen(
    viewModel: BottleFeedViewModel,
    onBack: () -> Unit
) {
    // Back gesture handler
    BackHandler {
        onBack()
    }

    // Ensure ViewModel is in sync with notification actions
    LaunchedEffect(Unit) {
        viewModel.checkExternalFinishOrCancel()
    }

    // Reload timer state from settings on every screen resume
    LaunchedEffect(Unit) {
        viewModel.reloadTimerStateFromSettings()
    }

    var showBottleList by remember { mutableStateOf(false) }
    var showOuncesSheet by remember { mutableStateOf(false) }
    var notesCardColor by remember { mutableStateOf(Color.Blue) }
    var notesTextColor by remember { mutableStateOf(Color.White) }

    // Animation states
    var backButtonVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var bottlesCardVisible by remember { mutableStateOf(false) }
    var ouncesCardVisible by remember { mutableStateOf(false) }
    var notesCardVisible by remember { mutableStateOf(false) }
    var timerCardVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val dismissNotifications = LocalDismissNotifications.current

    LaunchedEffect(Unit) {
        backButtonVisible = true
        delay(100)
        titleVisible = true
        delay(150)
        bottlesCardVisible = true
        delay(150)
        ouncesCardVisible = true
        delay(150)
        notesCardVisible = true
        delay(150)
        timerCardVisible = true
        delay(150)
        buttonVisible = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Back button
        AnimatedVisibility(
            visible = backButtonVisible,
            enter = fadeIn(animationSpec = tween(400, easing = EaseOutCubic)) +
                    slideInHorizontally(
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
                // Title
                AnimatedVisibility(
                    visible = titleVisible,
                    enter = fadeIn(animationSpec = tween(500, easing = EaseOutCubic)) +
                            slideInVertically(
                                animationSpec = tween(500, easing = EaseOutCubic),
                                initialOffsetY = { -it / 2 }
                            )
                ) {
                    Text(
                        "Bottle Feed",
                        fontSize = 24.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Timer message
                if (viewModel.isTimerRunning) {
                    Text(
                        "You can amend the ounces and notes before the bottle feed is done.",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Bottles taken today card
                AnimatedVisibility(
                    visible = bottlesCardVisible,
                    enter = fadeIn(animationSpec = tween(400, easing = EaseOutCubic)) +
                            slideInHorizontally(
                                animationSpec = tween(400, easing = EaseOutCubic),
                                initialOffsetX = { it }
                            )
                ) {
                    BottlesTakenCard(
                        count = viewModel.analytics.bottlesTakenToday,
                        onClick = { showBottleList = true }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ounces card
                AnimatedVisibility(
                    visible = ouncesCardVisible,
                    enter = fadeIn(animationSpec = tween(400, easing = EaseOutCubic)) +
                            slideInHorizontally(
                                animationSpec = tween(400, easing = EaseOutCubic),
                                initialOffsetX = { -it }
                            )
                ) {
                    OuncesCard(
                        ounces = viewModel.ounces,
                        onOuncesChange = { viewModel.ounces = it },
                        onLongPress = { showOuncesSheet = true }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes card
                AnimatedVisibility(
                    visible = notesCardVisible,
                    enter = fadeIn(animationSpec = tween(400, easing = EaseOutCubic)) +
                            slideInHorizontally(
                                animationSpec = tween(400, easing = EaseOutCubic),
                                initialOffsetX = { it }
                            )
                ) {
                    NotesCard(
                        notes = viewModel.notes,
                        onNotesChange = { viewModel.notes = it },
                        cardColor = notesCardColor,
                        textColor = notesTextColor,
                        onColorChange = { color, textColor ->
                            notesCardColor = color
                            notesTextColor = textColor
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Timer card
                AnimatedVisibility(
                    visible = timerCardVisible,
                    enter = fadeIn(animationSpec = tween(400, easing = EaseOutCubic)) +
                            slideInVertically(
                                animationSpec = tween(400, easing = EaseOutCubic),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    TimerCard(
                        isRunning = viewModel.isTimerRunning,
                        duration = viewModel.bottleDuration
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action button
                AnimatedVisibility(
                    visible = buttonVisible,
                    enter = fadeIn(animationSpec = tween(400, easing = EaseOutCubic)) +
                            slideInVertically(
                                animationSpec = tween(400, easing = EaseOutCubic),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    var buttonScale by remember { mutableStateOf(1f) }

                    Button(
                        onClick = {
                            val wasRunning = viewModel.isTimerRunning
                            if (viewModel.isTimerRunning) {
                                viewModel.stopTimer()
                            } else {
                                viewModel.startTimer()
                            }
                            // Add scale animation
                            buttonScale = 0.95f
                            coroutineScope.launch {
                                delay(100)
                                buttonScale = 1f
                            }
                            // If we just finished a feed, dismiss notifications
                            if (wasRunning && !viewModel.isTimerRunning) {
                                dismissNotifications()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(buttonScale),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (viewModel.buttonColor) {
                                BottleFeedButtonColor.GREEN -> Color(0xFF388E3C)
                                BottleFeedButtonColor.ORANGE -> Color(0xFFFFA500)
                                BottleFeedButtonColor.RED -> Color.Red
                            }
                        )
                    ) {
                        Text(
                            viewModel.buttonLabel,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }

    // Alerts
    if (viewModel.showSuccessAlert) {
        CustomAlert(
            show = viewModel.showSuccessAlert,
            icon = "✅",
            title = "Success",
            message = "Bottle recorded successfully",
            color = Color(0xFF388E3C),
            onDismiss = { viewModel.dismissAlerts() }
        )
    }

    if (viewModel.showWarningAlert) {
        CustomAlert(
            show = viewModel.showWarningAlert,
            icon = "⚠️",
            title = "Invalid ounces",
            message = "Please set ounces to a value greater than 0",
            color = Color(0xFFFFA500),
            onDismiss = { viewModel.dismissAlerts() }
        )
    }

    if (viewModel.showErrorAlert) {
        CustomAlert(
            show = viewModel.showErrorAlert,
            icon = "❌",
            title = "Error",
            message = "Couldn't save the bottle, please try again",
            color = Color.Red,
            onDismiss = { viewModel.dismissAlerts() }
        )
    }

    // Bottle list sheet
    if (showBottleList) {
        BottleListSheet(
            todayFeeds = viewModel.todayFeeds,
            yesterdayFeeds = viewModel.yesterdayFeeds,
            olderFeeds = viewModel.olderFeeds,
            onDismiss = { showBottleList = false },
            onDelete = { viewModel.deleteFeed(it) }
        )
    }

    // Ounces sheet
    if (showOuncesSheet) {
        OuncesSheet(
            ounces = viewModel.ounces,
            onOuncesChange = { viewModel.ounces = it },
            onDismiss = { showOuncesSheet = false }
        )
    }
} 