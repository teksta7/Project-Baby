package com.teksta.projectparent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopMiniCardView(
    average: String = "Need more data to calculate average",
    topText: String = "Average bottle feed time:",
    onTap: () -> Unit = {}
) {
    var displayAverage by remember { mutableStateOf(average) }
    
    LaunchedEffect(average) {
        displayAverage = average
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 16.dp)
            .background(
                color = Color(0xFF3F51B5), // indigo
                shape = RoundedCornerShape(25.dp)
            )
            .clickable { onTap() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Note: In a real implementation, you'd use an icon here
                // For now, we'll use text as a placeholder
                Text(
                    text = "ℹ",
                    color = Color.White,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = topText,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = displayAverage,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = "Press here for charts",
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
} 