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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
actual fun ProfileImagePicker(
    imageUri: String?,
    onImageSelected: (String?) -> Unit
) {
    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // Copy the picked image to internal storage
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "profile.jpg")
            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            onImageSelected(file.absolutePath)
        } else {
            onImageSelected(null)
        }
    }
    Button(
        onClick = { imagePickerLauncher.launch("image/*") },
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text("Select a profile picture")
    }
    imageUri?.let { uriString ->
        val file = File(uriString)
        val bitmap = remember(uriString) {
            if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
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