package com.teksta.projectparent

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun SettingsScreen(onBack: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    CommonSettingsScreen(
        onBack = onBack,
        snackbarHostState = snackbarHostState,
        coroutineScope = coroutineScope,
        context = context
    )
    SnackbarHost(hostState = snackbarHostState)
}
