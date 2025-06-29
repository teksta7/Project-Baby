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
import kotlinx.datetime.LocalDateTime
import com.teksta.projectparent.DatePickerField
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
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Boy") }
    var birthDate by remember { mutableStateOf("") }
    var weightLbs by remember { mutableStateOf(0) }
    var weightOz by remember { mutableStateOf(0) }
    var showError by remember { mutableStateOf(false) }
    var profileImageUri by remember { mutableStateOf<String?>(null) }

    // Lottie animation setup (Android only)
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("Animation.json"))
    val progress by animateLottieCompositionAsState(composition)

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
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie animation at the top
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(180.dp)
                )
                Text("Create a Baby Profile", fontSize = 24.sp, color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Baby's Name", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
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
                DatePickerField(
                    label = "Select Birth Date",
                    date = birthDate,
                    onDateSelected = { birthDate = it }
                )
                // Profile picture picker (expect/actual)
                ProfileImagePicker(
                    imageUri = profileImageUri,
                    onImageSelected = { profileImageUri = it }
                )
                Button(
                    onClick = {
                        if (name.isNotBlank() && birthDate.isNotBlank()) {
                            val weight = "${weightLbs} lbs ${weightOz} oz"
                            onComplete(name, gender, birthDate, weight, profileImageUri)
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

@Composable
fun Stepper(value: Int, onValueChange: (Int) -> Unit, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = { onValueChange(value - 1) }, enabled = value > 0) { Text("-") }
        Text(" $value $label ", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
        Button(onClick = { onValueChange(value + 1) }) { Text("+") }
    }
} 