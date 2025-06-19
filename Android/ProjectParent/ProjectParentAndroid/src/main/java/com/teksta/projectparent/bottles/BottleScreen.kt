package com.teksta.projectparent.bottles

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.teksta.projectparent.android.R
import com.teksta.projectparent.android.services.BottleTrackingService
import com.teksta.projectparent.models.BottlesViewModel

@Composable
fun BottlesScreen(
    viewModel: BottlesViewModel,
    onNavigateToBottleList: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    // This effect handles navigation requests from the ViewModel
    LaunchedEffect(uiState.showBottleListScreen) {
        if (uiState.showBottleListScreen) {
            onNavigateToBottleList()
            viewModel.bottleListNavigationComplete() // Reset the trigger after navigation
        }
    }

    // This effect handles showing snackbar alerts from the ViewModel
    LaunchedEffect(uiState.alertMessage) {
        uiState.alertMessage?.let {
            scaffoldState.snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.dismissAlert()
        }
    }

    // This effect manages the lifecycle of the BroadcastReceiver
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BottleTrackingService.ACTION_TIMER_UPDATE) {
                    val duration = intent.getIntExtra(BottleTrackingService.EXTRA_DURATION_SECONDS, 0)
                    viewModel.updateDurationFromBroadcast(duration)
                }
            }
        }

        val filter = IntentFilter(BottleTrackingService.ACTION_TIMER_UPDATE)
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        // onDispose is called when the composable leaves the screen
        onDispose {
            context.unregisterReceiver(receiver)
            viewModel.onDispose() // Also notify the ViewModel
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.statusBarsPadding(),
        topBar = { TopAppBar(title = { Text("Log Bottle Feed") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
//            Image(
//                painter = painterResource(id = com.teksta.projectparent.android.R.drawable.bottle_image), // Your placeholder image
//                contentDescription = "Bottle",
//                modifier = Modifier
//                    .size(150.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )

            Text(
                text = if (uiState.isTimerRunning) "You can amend ounces and notes during the feed." else "Tap Start to begin.",
                style = MaterialTheme.typography.caption
            )

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FeatureCard(
                    title = "Bottles Today",
                    value = uiState.bottlesTakenToday.toString(),
                    onClick = viewModel::requestBottleListNavigation,
                    modifier = Modifier.weight(1f)
                )
                FeatureCard(
                    title = "Ounces (oz)",
                    value = String.format("%.2f", uiState.ouncesToSave),
                    onClick = { viewModel.toggleOuncesDialog(true) },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = uiState.notesToSave,
                onValueChange = viewModel::onNotesChanged,
                label = { Text("Notes for this bottle") },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = viewModel.formatDuration(uiState.bottleDurationSeconds),
                style = MaterialTheme.typography.h4
            )

            Button(
                onClick = viewModel::startOrFinishBottleFeed,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(uiState.bottleFeedButtonColorValue))
            ) {
                Text(uiState.bottleFeedButtonLabel, color = Color.White)
            }
        }
    }

    if (uiState.showOuncesStepperDialog) {
        OuncesInputDialog(
            currentOunces = uiState.ouncesToSave,
            onDismiss = { viewModel.toggleOuncesDialog(false) },
            onConfirm = { newOunces ->
                viewModel.onOuncesChanged(newOunces)
                viewModel.toggleOuncesDialog(false)
            }
        )
    }
}

@Composable
fun FeatureCard(title: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.clickable(onClick = onClick), elevation = 4.dp) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.subtitle2)
            Text(value, style = MaterialTheme.typography.h5)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OuncesInputDialog(
    currentOunces: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var tempOunces by remember { mutableStateOf(currentOunces.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Ounces") },
        text = {
            OutlinedTextField(
                value = tempOunces,
                onValueChange = { tempOunces = it },
                label = { Text("Ounces") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(tempOunces.toDoubleOrNull() ?: currentOunces) }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}