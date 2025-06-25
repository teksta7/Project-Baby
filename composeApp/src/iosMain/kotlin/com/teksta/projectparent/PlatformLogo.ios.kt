package com.teksta.projectparent

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.Color

@Composable
actual fun appLogoPainter(): Painter {
    return ColorPainter(Color.Gray)
} 