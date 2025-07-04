package com.teksta.projectparent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onBack: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image (show only image or default avatar)
        val context = LocalContext.current
        val imageUri = viewModel.profileImageUri
        Box(
            modifier = Modifier
                .size(screenHeight / 5.5f)
                .clip(CircleShape)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUri.isNullOrBlank()) {
                val file = File(imageUri)
                val bitmap = remember(imageUri) {
                    if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("👶", fontSize = 64.sp)
                }
            } else {
                Text("👶", fontSize = 64.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Baby Name
        Text(
            text = viewModel.babyName,
            color = Color.White,
            fontSize = 24.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Age Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(screenHeight / 8)
                .clickable { viewModel.onAgeCardTap() },
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF388E3C)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = viewModel.ageLabel,
                        color = Color.White,
                        fontSize = 36.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = viewModel.ageLabelWords,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Age:",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = viewModel.ageSubLabel,
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Weight Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(screenHeight / 8)
                .clickable { viewModel.onWeightCardTap() },
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF388E3C)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = viewModel.weightLabel,
                        color = Color.White,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = viewModel.weightLabelWords,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Weight:",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = viewModel.weightSubLabel,
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        // Back button
        Text(
            text = "< Back",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier
                .clickable { onBack() }
                .padding(8.dp)
        )
    }
} 