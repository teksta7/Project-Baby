package com.teksta.projectparent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

@Composable
fun CardSelectionOnboardingScreen(
    initialSelection: CardSelectionState = CardSelectionState(),
    onComplete: (CardSelectionState) -> Unit
) {
    var bottles by remember { mutableStateOf(initialSelection.bottles) }
    var sleep by remember { mutableStateOf(initialSelection.sleep) }
    var food by remember { mutableStateOf(initialSelection.food) }
    var meds by remember { mutableStateOf(initialSelection.meds) }
    var wind by remember { mutableStateOf(initialSelection.wind) }
    var poo by remember { mutableStateOf(initialSelection.poo) }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Card Selection", fontSize = 24.sp, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
            Text("Select which baby stat cards to enable.", fontSize = 16.sp, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
            ToggleRow("Bottles", bottles) { bottles = it }
            ToggleRow("Sleep", sleep) { sleep = it }
            ToggleRow("Food", food) { food = it }
            ToggleRow("Meds", meds) { meds = it }
            ToggleRow("Wind", wind) { wind = it }
            ToggleRow("Poo", poo) { poo = it }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = { onComplete(CardSelectionState(bottles, sleep, food, meds, wind, poo)) }, modifier = Modifier.fillMaxWidth()) {
                Text("Complete card selection", color = Color.White)
            }
        }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
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