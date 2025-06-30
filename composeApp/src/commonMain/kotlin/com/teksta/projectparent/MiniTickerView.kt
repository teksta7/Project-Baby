package com.teksta.projectparent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MiniTickerView(bottleFeedViewModel: BottleFeedViewModel? = null) {
    var messageIndex by remember { mutableStateOf(0) }
    var colorIndex by remember { mutableStateOf(0) }
    
    val analytics = bottleFeedViewModel?.analytics
    
    val messages = remember(analytics) {
        listOf(
            "Next bottle is due: ${analytics?.nextBottleDue ?: "N/A"}",
            "Wake windows today: N/A",
            "Average time between bottles: ${analytics?.averageTimeBetweenBottles ?: "N/A"}",
            "Food eaten today: N/A",
            "Any medicine due: N/A",
            "Amount of wind: N/A",
            "Amount of nappy changes: N/A"
        )
    }
    
    val colors = listOf(
        Color(0xFF2196F3), // blue
        Color(0xFF9C27B0), // purple
        Color(0xFF3F51B5)  // indigo
    )
    
    val currentMessage = messages[messageIndex]
    val currentColor = colors[colorIndex]
    
    // Timer effect to rotate messages every 3 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            messageIndex = (messageIndex + 1) % messages.size
            colorIndex = (colorIndex + 1) % colors.size
        }
    }
    
    Box(
        modifier = Modifier
            .width(HomeCardWidth)
            .height(60.dp)
            .background(
                color = currentColor,
                shape = RoundedCornerShape(15.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Note: In a real implementation, you'd use an icon here
            // For now, we'll use text as a placeholder
            Text(
                text = "👉",
                color = Color.White,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = currentMessage,
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        }
    }
} 