package com.teksta.projectparent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottleFeedScreen(
    viewModel: BottleFeedViewModel,
    onBack: () -> Unit
) {
    // Back gesture handler
    BackHandler {
        onBack()
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
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(buttonScale),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (viewModel.buttonColor) {
                                BottleFeedButtonColor.GREEN -> Color.Green
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
            color = Color.Green,
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
            feeds = viewModel.feeds,
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

@Composable
fun BottlesTakenCard(count: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (count > 0) Color.Green else Color.Blue
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    count.toString(),
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    "Bottles taken today",
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    "Press for bottle history",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun OuncesCard(
    ounces: Double,
    onOuncesChange: (Double) -> Unit,
    onLongPress: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onLongPress() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFA500))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "${ounces} oz",
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}

@Composable
fun NotesCard(
    notes: String,
    onNotesChange: (String) -> Unit,
    cardColor: Color,
    textColor: Color,
    onColorChange: (Color, Color) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        BasicTextField(
            value = notes,
            onValueChange = { 
                onNotesChange(it)
                if (it.isNotEmpty()) {
                    onColorChange(Color.Yellow, Color.Black)
                } else {
                    onColorChange(Color.Blue, Color.White)
                }
            },
            textStyle = androidx.compose.ui.text.TextStyle(
                color = textColor,
                fontSize = 16.sp
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            decorationBox = { innerTextField ->
                if (notes.isEmpty()) {
                    Text(
                        "Add notes for bottle...",
                        color = textColor.copy(alpha = 0.6f),
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
fun TimerCard(isRunning: Boolean, duration: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Blue)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isRunning) {
                val minutes = duration / 60
                val seconds = duration % 60
                Text(
                    "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}",
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            } else {
                Text(
                    "00:00",
                    fontSize = 32.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CustomAlert(
    show: Boolean,
    icon: String,
    title: String,
    message: String,
    color: Color,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            containerColor = Color.White,
            titleContentColor = color,
            textContentColor = Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottleListSheet(
    feeds: List<BottleFeedUiModel>,
    onDismiss: () -> Unit,
    onDelete: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "Bottle History (${feeds.size} feeds)",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(feeds) { feed ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Given at: ${feed.formattedStartTime}",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    "Ounces: ${feed.ounces}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Duration: ${feed.formattedDuration}",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp
                                )
                                if (feed.notes.isNotEmpty()) {
                                    Text(
                                        "Notes: ${feed.notes}",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp
                                    )
                                } else {
                                    Text(
                                        "Notes: (none)",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            TextButton(
                                onClick = { onDelete(feed.id) }
                            ) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OuncesSheet(
    ounces: Double,
    onOuncesChange: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Set Ounces",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onOuncesChange((ounces - 0.25).coerceAtLeast(0.0)) }
                ) {
                    Text("-", fontSize = 20.sp)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    "${ounces} oz",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Button(
                    onClick = { onOuncesChange(ounces + 0.25) }
                ) {
                    Text("+", fontSize = 20.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
} 