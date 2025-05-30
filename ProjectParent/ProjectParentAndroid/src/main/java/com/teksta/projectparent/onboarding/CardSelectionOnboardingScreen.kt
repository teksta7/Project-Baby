package com.teksta.projectparent.onboarding

// In androidApp/src/main/java/com/teksta/projectparent/onboarding/CardSelectionOnboardingScreen.kt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun CardSelectionOnboardingScreen(
    viewModel: CardSelectionViewModel,
    onDismissRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Card Selection") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Card Selection",
                fontSize = 24.sp,
                style = MaterialTheme.typography.h5
            )
            Text(
                "Here you can select which baby stat cards to enable, " +
                        "each of which enable you to track different stats about your baby.",
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                "You can change this at any time in the settings.",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CardToggleItem("Bottles", uiState.isBottlesCardTracked, viewModel::onBottlesCardToggled)
            CardToggleItem("Sleep", uiState.isSleepCardTracked, viewModel::onSleepCardToggled)
            // Add other toggles similarly for Food, Meds, Wind, Poo if they are being implemented

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Bottle Notifications", style = MaterialTheme.typography.h6)
            CardToggleItem("Enable Next Bottle Feed Notification", uiState.enableBottleNotification) { isEnabled ->
                coroutineScope.launch {
                    viewModel.onEnableBottleNotificationToggled(isEnabled)
                }
            }


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.onSettingsComplete()
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text("Complete Card Selection")
            }
        }
    }
}

@Composable
private fun CardToggleItem(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}