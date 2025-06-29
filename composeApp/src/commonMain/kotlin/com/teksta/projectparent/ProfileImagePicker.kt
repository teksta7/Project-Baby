package com.teksta.projectparent

import androidx.compose.runtime.Composable

@Composable
expect fun ProfileImagePicker(
    imageUri: String?,
    onImageSelected: (String?) -> Unit
) 