package com.teksta.projectparent

import androidx.compose.runtime.Composable

@Composable
expect fun DatePickerField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) 