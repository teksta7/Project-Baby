package com.teksta.projectparent

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
actual fun ProfileImagePicker(
    imageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        onImageSelected(uri?.toString())
    }
    Button(
        onClick = { imagePickerLauncher.launch("image/*") },
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text("Select a profile picture")
    }
    imageUri?.let { uriString ->
        val uri = Uri.parse(uriString)
        val bitmap = remember(uriString) {
            val stream = context.contentResolver.openInputStream(uri)
            stream?.use { BitmapFactory.decodeStream(it) }
        }
        bitmap?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .height(80.dp)
                    .clip(CircleShape)
            )
        }
    }
} 