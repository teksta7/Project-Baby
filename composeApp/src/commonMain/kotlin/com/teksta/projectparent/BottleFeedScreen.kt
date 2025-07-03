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
import androidx.compose.runtime.Composable

@Composable
expect fun BottleFeedScreen(
    viewModel: BottleFeedViewModel,
    onBack: () -> Unit
)

@Composable
fun BottlesTakenCard(count: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (count > 0) Color(0xFF388E3C) else Color.Blue
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
    todayFeeds: List<BottleFeedUiModel>,
    yesterdayFeeds: List<BottleFeedUiModel>,
    olderFeeds: List<BottleFeedUiModel>,
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
            val totalFeeds = todayFeeds.size + yesterdayFeeds.size + olderFeeds.size
            Text(
                "Bottle History ($totalFeeds feeds)",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                if (todayFeeds.isNotEmpty()) {
                    item {
                        Text("Today", color = Color.Cyan, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    items(todayFeeds) { feed ->
                        BottleFeedHistoryCard(feed, onDelete)
                    }
                }
                if (yesterdayFeeds.isNotEmpty()) {
                    item {
                        Text("Yesterday", color = Color.Cyan, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    items(yesterdayFeeds) { feed ->
                        BottleFeedHistoryCard(feed, onDelete)
                    }
                }
                if (olderFeeds.isNotEmpty()) {
                    item {
                        Text("Older", color = Color.Cyan, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    items(olderFeeds) { feed ->
                        BottleFeedHistoryCard(feed, onDelete)
                    }
                }
            }
        }
    }
}

@Composable
fun BottleFeedHistoryCard(feed: BottleFeedUiModel, onDelete: (String) -> Unit) {
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