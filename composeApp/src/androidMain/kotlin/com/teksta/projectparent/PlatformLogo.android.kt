package com.teksta.projectparent

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.teksta.projectparent.R

@Composable
actual fun appLogoPainter(): Painter {
    return painterResource(id = R.drawable.app_logo)
} 