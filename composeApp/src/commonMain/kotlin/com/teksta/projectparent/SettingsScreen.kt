package com.teksta.projectparent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.window.Dialog
import com.teksta.projectparent.HomeCards
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.saveable.listSaver
import com.russhwolf.settings.Settings
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import com.teksta.projectparent.requestScheduleExactAlarmPermissionIfNeeded
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable

@Composable
expect fun SettingsScreen(onBack: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonSettingsScreen(
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    context: Any
) {
    // --- State ---
    val settings = remember { Settings() }
    val cardTogglesKey = "card_toggles"
    val defaultToggles = listOf(true, false, false, false, false, false, false)

    fun saveTogglesToSettings(toggles: List<Boolean>) {
        val str = toggles.joinToString(",") { if (it) "1" else "0" }
        settings.putString(cardTogglesKey, str)
    }
    fun loadTogglesFromSettings(): List<Boolean> {
        val str = settings.getStringOrNull(cardTogglesKey)
        return if (str != null) {
            str.split(",").map { it == "1" }
        } else {
            defaultToggles
        }
    }

    var isAdsRemoved by rememberSaveable { mutableStateOf(false) }
    var isDonateDialogOpen by remember { mutableStateOf(false) }
    var isRestoreDialogOpen by remember { mutableStateOf(false) }
    var isDurationDialogOpen by remember { mutableStateOf(false) }
    var isOuncesDialogOpen by remember { mutableStateOf(false) }
    var bottleNotificationEnabled by rememberSaveable {
        mutableStateOf(settings.getBoolean("bottle_notification_enabled", false))
    }
    var liveActivityEnabled by rememberSaveable { mutableStateOf(false) }
    var durationMinutes by rememberSaveable {
        mutableStateOf(settings.getInt("bottle_notification_duration", 180)) // 3 hours default
    }
    var defaultOunces by rememberSaveable {
        mutableStateOf(settings.getString("default_ounces", "4.0").toDoubleOrNull() ?: 4.0)
    }
    var defaultNote by rememberSaveable {
        mutableStateOf(settings.getString("default_bottle_note", ""))
    }

    val initialToggles = loadTogglesFromSettings()
    val cardToggles = rememberSaveable(
        saver = listSaver(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) {
        initialToggles.toMutableStateList()
    }
    val cardLabels = listOf("Bottles", "Sleep", "Food", "Meds", "Wind", "Poo", "Test")
    val cardDescriptions = listOf(
        "Enable the app to track the number of bottles you have given to your baby",
        "Enable the app to track the amount sleep your baby has had",
        "Enable the app to track the amount of food you give to your baby",
        "Enable the app to track any medicine administered to your baby",
        "Enable the app to track the amount of wind your baby has had",
        "Enable the app to track any nappy changes your baby has had",
        "EXPERIMENTAL"
    )

    // --- Bottle Feed Settings ---
    val bottleNotificationKey = "bottle_notification_enabled"
    val bottleNotificationDurationKey = "bottle_notification_duration"
    val defaultOuncesKey = "default_ounces"
    val defaultNoteKey = "default_bottle_note"

    fun saveBottleSettings() {
        settings.putBoolean(bottleNotificationKey, bottleNotificationEnabled)
        settings.putInt(bottleNotificationDurationKey, durationMinutes)
        settings.putString(defaultOuncesKey, defaultOunces.toString())
        settings.putString(defaultNoteKey, defaultNote)
    }

    fun onToggleBottleNotification(enabled: Boolean) {
        bottleNotificationEnabled = enabled
        saveBottleSettings()
        if (enabled) {
            requestScheduleExactAlarmPermissionIfNeeded(context) { granted ->
                if (!granted) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Permission required to schedule bottle feed notifications. Please enable in system settings.")
                    }
                }
            }
        }
    }

    // --- UI ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Information ---
            Text("Information", color = Color.Gray, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("App Version", color = Color.White)
                    Text("1.0.0", color = Color.LightGray)
                }
            }

            // --- Purchase/Donation ---
            Text("Purchase Options", color = Color.Gray, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isAdsRemoved) {
                        Text("Thank you for donating!", color = Color.Green)
                    } else {
                        Button(onClick = { isDonateDialogOpen = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Donate", color = Color.White)
                        }
                        Button(onClick = { isRestoreDialogOpen = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Restore Purchases", color = Color.White)
                        }
                    }
                }
            }

            // --- Baby Stat Cards ---
            Text("Baby stat cards", color = Color.Gray, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cardLabels.forEachIndexed { idx, label ->
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = cardToggles[idx],
                                    onCheckedChange = {
                                        cardToggles[idx] = it
                                        saveTogglesToSettings(cardToggles)
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Cyan)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(label, color = Color.White, fontWeight = FontWeight.Medium)
                            }
                            Text(cardDescriptions[idx], color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(start = 48.dp, bottom = 8.dp))
                        }
                    }
                }
            }

            // --- Bottles ---
            Text("Bottles", color = Color.Gray, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = bottleNotificationEnabled,
                            onCheckedChange = { onToggleBottleNotification(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Cyan)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Next bottle feed notification", color = Color.White)
                            Text(
                                "This will notify you when the next bottle feed is due using the duration you have given below",
                                color = Color.LightGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {}, // No dialog, just show slider inline
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
                        ) {
                            Text("Select a duration", color = Color.Black)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("${durationMinutes} Minutes", color = Color.White)
                    }
                    Slider(
                        value = durationMinutes.toFloat(),
                        onValueChange = {
                            val rounded = (it / 5).toInt() * 5
                            durationMinutes = rounded
                            saveBottleSettings()
                        },
                        valueRange = 10f..360f,
                        steps = ((360 - 10) / 5) - 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = {}, // No dialog, just show slider inline
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
                        ) {
                            Text("Set default ounces", color = Color.Black)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(String.format("%.2f oz", defaultOunces), color = Color.White)
                    }
                    Slider(
                        value = defaultOunces.toFloat(),
                        onValueChange = {
                            val rounded = (it * 4).toInt() / 4f
                            defaultOunces = rounded.toDouble()
                            saveBottleSettings()
                        },
                        valueRange = 0.0f..10.0f,
                        steps = ((10.0 / 0.25).toInt()) - 1
                    )
                    OutlinedTextField(
                        value = defaultNote,
                        onValueChange = {
                            defaultNote = it
                            saveBottleSettings()
                        },
                        label = { Text("Set default note", color = Color.Cyan) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(color = Color.White)
                    )
                }
            }

            // --- Placeholders for other sections ---
            Text("Sleep, Food, Medicine, Wind, Nappies (more coming soon...)", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }

    // --- Dialogs ---
    if (isDonateDialogOpen) {
        Dialog(onDismissRequest = { isDonateDialogOpen = false }) {
            Card(Modifier.padding(24.dp)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Donate (stub)", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        isAdsRemoved = true
                        isDonateDialogOpen = false
                    }) { Text("Simulate Donation") }
                }
            }
        }
    }
    if (isRestoreDialogOpen) {
        Dialog(onDismissRequest = { isRestoreDialogOpen = false }) {
            Card(Modifier.padding(24.dp)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Restore Purchases (stub)", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        isAdsRemoved = true
                        isRestoreDialogOpen = false
                    }) { Text("Simulate Restore") }
                }
            }
        }
    }
    if (isDurationDialogOpen) {
        Dialog(onDismissRequest = { isDurationDialogOpen = false }) {
            Card(Modifier.padding(24.dp)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Select Duration (minutes)", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Slider(
                        value = durationMinutes.toFloat(),
                        onValueChange = { durationMinutes = it.toInt() },
                        valueRange = 10f..360f,
                        steps = 35
                    )
                    Text("${durationMinutes} Minutes")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { isDurationDialogOpen = false }) { Text("Done") }
                }
            }
        }
    }
    if (isOuncesDialogOpen) {
        Dialog(onDismissRequest = { isOuncesDialogOpen = false }) {
            Card(Modifier.padding(24.dp)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Set Default Ounces", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Slider(
                        value = defaultOunces.toFloat(),
                        onValueChange = { defaultOunces = it.toDouble() },
                        valueRange = 0.0f..10.0f,
                        steps = 40
                    )
                    Text(String.format("%.2f oz", defaultOunces))
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { isOuncesDialogOpen = false }) { Text("Done") }
                }
            }
        }
    }
} 