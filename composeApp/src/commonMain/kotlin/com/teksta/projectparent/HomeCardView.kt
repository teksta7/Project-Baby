package com.teksta.projectparent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeCardView(
    homeCard: HomeCard,
    onCardClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        homeCard.color,
                        homeCard.color.copy(alpha = 0.8f)
                    )
                )
            )
            .clickable { onCardClick(homeCard.viewString) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Note: In a real implementation, you'd use actual icons here
            // For now, we'll use text as placeholders for the icons
            val iconText = when (homeCard.imageToDisplay) {
                "figure.child" -> "👶"
                "waterbottle" -> "🍼"
                "powersleep" -> "😴"
                "carrot" -> "🥕"
                "pill" -> "💊"
                "wind" -> "💨"
                "toilet" -> "🚽"
                "gear" -> "⚙️"
                "testtube.2" -> "🧪"
                else -> "📱"
            }
            
            Text(
                text = iconText,
                fontSize = 80.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = homeCard.presentedString,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
} 