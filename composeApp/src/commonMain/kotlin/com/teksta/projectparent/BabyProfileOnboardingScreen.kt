package com.teksta.projectparent

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
//import androidx.compose.ui.text.style.LocalTextStyle

@Composable
fun BabyProfileOnboardingScreen(
    onComplete: (name: String, gender: String, birthDate: String, weight: String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Boy") }
    var birthDate by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
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
                Text("Create a Baby Profile", fontSize = 24.sp, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Baby's Name", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Gender:", color = Color.White, modifier = Modifier.padding(end = 8.dp))
                    DropdownMenuBox(selected = gender, options = listOf("Boy", "Girl")) { gender = it }
                }
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { birthDate = it },
                    label = { Text("Birth Date (YYYY-MM-DD)", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Birth Weight (lbs oz)", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                // Placeholder for profile picture picker
                Text("Profile picture picker coming soon...", color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
                Button(
                    onClick = {
                        if (name.isNotBlank() && birthDate.isNotBlank() && weight.isNotBlank()) {
                            onComplete(name, gender, birthDate, weight)
                        } else {
                            showError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Complete baby profile", color = Color.White)
                }
                if (showError) {
                    Text("Please fill all required fields.", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
                    LaunchedEffect(showError) {
                        kotlinx.coroutines.delay(1500)
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