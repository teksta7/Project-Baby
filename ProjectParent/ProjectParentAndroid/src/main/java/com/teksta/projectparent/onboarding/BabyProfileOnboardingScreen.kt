package com.teksta.projectparent.onboarding

// In androidApp/src/main/java/com/teksta/projectparent/onboarding/BabyProfileOnboardingScreen.kt

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildFriendly
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.material.datepicker.MaterialDatePicker
import androidx.compose.material3.DatePicker // M3
import androidx.compose.material3.DatePickerDialog // M3
import androidx.compose.material3.ExperimentalMaterial3Api // M3
import androidx.compose.material3.TextButton // M3
import androidx.compose.material3.rememberDatePickerState // M3
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import androidx.compose.foundation.clickable // Make sure this is imported
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage // <<< IMPORT COIL



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyProfileOnboardingScreen(
    viewModel: BabyProfileViewModel,
    onDismissRequest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDatePickerDialog by remember { mutableStateOf(false) }


    // This launcher replaces PhotosPicker in SwiftUI
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? -> viewModel.onImageSelected(uri?.toString()) }
    )

    // Effect to close the screen once saving is complete
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onDismissRequest()
        }
    }

    Scaffold(
        topBar = { TopAppBar(modifier = Modifier.statusBarsPadding(),
            title = { Text("Create a Baby Profile") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .statusBarsPadding()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.selectedImageUri != null) {
                AsyncImage(
                    model = Uri.parse(uiState.selectedImageUri), // Load image from Uri string
                    contentDescription = "Selected Baby Profile Picture",
                    modifier = Modifier
                        .size(120.dp) // Make it a bit larger
                        .clip(CircleShape), // Clip to a circle
                    contentScale = ContentScale.Crop // Crop to fill the circle
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChildFriendly,
                    contentDescription = "Baby Profile Icon Placeholder",
                    modifier = Modifier.size(100.dp)
                )
            }
            // --- End of image display ---

            OutlinedTextField(
                value = uiState.babyName,
                onValueChange = viewModel::onNameChanged,
                label = { Text("What is your baby's name?") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.validationError?.contains("name") == true
            )

            Text("What was their birth weight?")
            WeightStepper(
                label = "lbs",
                value = uiState.weightPounds,
                onValueChanged = viewModel::onWeightPoundsChanged,
                range = 0..20
            )
            WeightStepper(
                label = "oz",
                value = uiState.weightOunces,
                onValueChanged = viewModel::onWeightOuncesChanged,
                range = 0..15
            )

            GenderPicker(
                selectedGender = uiState.gender,
                onGenderSelected = viewModel::onGenderSelected

            )

//            // Button to launch the date picker dialog
//            Button(onClick = {
//                val datePicker = MaterialDatePicker.Builder.datePicker().build()
//                datePicker.addOnPositiveButtonClickListener { selection ->
//                    viewModel.onBirthDateSelected(selection)
//                }
//                // Requires FragmentActivity to show
//                (context as? androidx.fragment.app.FragmentActivity)?.let {
//                    datePicker.show(it.supportFragmentManager, datePicker.toString())
//                }
//            })
            // Button to launch the Compose date picker dialog
            Button(onClick = { showDatePickerDialog = true }) {
                // Option 1: Format kotlinx.datetime.LocalDate directly (Recommended if you don't need java.util.Date)
                // You might need to add kotlinx-datetime-format library or write a simple formatter
                val day = uiState.birthDate.dayOfMonth
                val month = uiState.birthDate.month.toString().lowercase().replaceFirstChar { it.titlecase() }
                val year = uiState.birthDate.year
                Text("Born on: $day $month $year")

                // Option 2: If you need to use java.text.SimpleDateFormat
                // val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                // val javaLocalDate = uiState.birthDate.toJavaLocalDate() // Convert to java.time.LocalDate
                // val dateToShow = Date.from(javaLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant()) // Convert to java.util.Date
                // Text("Born on: ${dateFormatter.format(dateToShow)}")
            }

            if (showDatePickerDialog) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = uiState.birthDate
                        .atTime(LocalTime(0, 0)) // Combine with LocalTime (midnight)
                        .toInstant(TimeZone.UTC)      // Convert LocalDateTime to Instant in UTC
                        .toEpochMilliseconds()
                )
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            showDatePickerDialog = false
                            datePickerState.selectedDateMillis?.let {
                                viewModel.onBirthDateSelected(it)
                            }
                        },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colors.background // Or your desired color e.g. Color.White or a specific theme color
                            ) ) { Text("OK") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePickerDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colors.background // Or your desired color e.g. Color.White or a specific theme color
                            )
                        ) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
//            {
//                Text("Born on: ${uiState.birthDate}")
//            }

            Button(onClick = { photoPickerLauncher.launch(
                androidx.activity.result.PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )}) {
                Text(if (uiState.selectedImageUri != null) "Change Profile Picture" else "Select a Profile Picture")
            }

            uiState.validationError?.let {
                Text(it, color = MaterialTheme.colors.error)
            }

            Button(
                onClick = viewModel::onSaveProfile,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Complete Baby Profile")
            }
        }
    }
}

@Composable
private fun GenderPicker(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(), // Make the whole row take available width
        verticalAlignment = Alignment.CenterVertically, // Vertically center "Gender:" and the options Row
        horizontalArrangement = Arrangement.Center // <<< ADD THIS LINE

    ) {
        Text(
            text = "Gender:",
            modifier = Modifier.padding(end = 16.dp) // Add some spacing after the "Gender:" label
        )
        Row( // This inner Row groups the "Boy" option
            verticalAlignment = Alignment.CenterVertically, // Vertically center RadioButton and "Boy"
            modifier = Modifier.clickable { onGenderSelected("Boy") } // Make the whole segment clickable
        ) {
            RadioButton(
                selected = (selectedGender == "Boy"),
                onClick = { onGenderSelected("Boy") }
            )
            Text("Boy")
        }
        Spacer(Modifier.width(16.dp)) // Spacing between "Boy" and "Girl" options
        Row( // This inner Row groups the "Girl" option
            verticalAlignment = Alignment.CenterVertically, // Vertically center RadioButton and "Girl"
            modifier = Modifier.clickable { onGenderSelected("Girl") } // Make the whole segment clickable
        ) {
            RadioButton(
                selected = (selectedGender == "Girl"),
                onClick = { onGenderSelected("Girl") }
            )
            Text("Girl")
        }
    }
}

@Composable
private fun WeightStepper(
    label: String,
    value: Int,
    onValueChanged: (Int) -> Unit,
    range: IntRange
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(onClick = { if (value > range.first) onValueChanged(value - 1) }) { Text("-") }
        Text("$value $label", modifier = Modifier.padding(horizontal = 16.dp))
        OutlinedButton(onClick = { if (value < range.last) onValueChanged(value + 1) }) { Text("+") }
    }
}