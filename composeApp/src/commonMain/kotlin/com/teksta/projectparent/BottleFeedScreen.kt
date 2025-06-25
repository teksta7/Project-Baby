package com.teksta.projectparent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun BottleFeedScreen(
    feeds: List<BottleFeedUiModel> = emptyList(),
    onAddFeed: (amount: String, note: String) -> Unit = { _, _ -> },
    onDeleteFeed: (id: Long) -> Unit = {},
    analytics: BottleFeedAnalyticsUiModel = BottleFeedAnalyticsUiModel()
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Bottle Feed", fontSize = 20.sp, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (ml or oz)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Note (optional)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        Button(
            onClick = {
                if (amount.isNotBlank()) {
                    onAddFeed(amount, note)
                    amount = ""
                    note = ""
                    showSuccess = true
                } else {
                    showError = true
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Save Feed")
        }
        if (showSuccess) {
            Text("Feed saved!", color = MaterialTheme.colorScheme.primary)
            LaunchedEffect(showSuccess) {
                kotlinx.coroutines.delay(1500)
                showSuccess = false
            }
        }
        if (showError) {
            Text("Please enter an amount.", color = MaterialTheme.colorScheme.error)
            LaunchedEffect(showError) {
                kotlinx.coroutines.delay(1500)
                showError = false
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Feed History", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(feeds.size) { idx ->
                val feed = feeds[idx]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Amount: ${feed.amount}")
                            Text("Time: ${feed.time}")
                            if (feed.note.isNotBlank()) Text("Note: ${feed.note}")
                        }
                        TextButton(onClick = { onDeleteFeed(feed.id) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Analytics", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
        Text("Average Amount: ${analytics.averageAmount}")
        Text("Average Time Between Feeds: ${analytics.averageTimeBetweenFeeds}")
        Text("Total Feeds Today: ${analytics.totalFeedsToday}")
    }
}

// UI models for preview and decoupling from DB
data class BottleFeedUiModel(
    val id: Long,
    val amount: String,
    val time: String,
    val note: String = ""
)

data class BottleFeedAnalyticsUiModel(
    val averageAmount: String = "-",
    val averageTimeBetweenFeeds: String = "-",
    val totalFeedsToday: String = "-"
) 