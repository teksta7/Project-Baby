package com.teksta.projectparent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun HomeView(
    onNavigateToSection: (String) -> Unit = {},
    onShowCharts: () -> Unit = {}
) {
    var showIndicator by remember { mutableStateOf(false) }
    var isRotationEnabled by remember { mutableStateOf(true) }
    var cardScale by remember { mutableStateOf(1.0f) }
    var isChartSheetPresented by remember { mutableStateOf(false) }
    var isCardSettingsSheetPresented by remember { mutableStateOf(false) }
    var localHomeCards by remember { mutableStateOf(HomeCards) }
    var currentCardIndex by remember { mutableStateOf(0) }
    val cardsToShow = localHomeCards.filter { it.toTrack }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // App Title (left-aligned)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, bottom = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Project Parent",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )
        }
        // Top Mini Card (centered)
        TopMiniCardView(
            average = "0 minutes 24 seconds", // Example value
            topText = "Average bottle feed time:",
            onTap = {
                isChartSheetPresented = true
                onShowCharts()
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
        // Main Card (centered, only one at a time)
        if (cardsToShow.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                HomeCardView(
                    homeCard = cardsToShow[currentCardIndex],
                    onCardClick = { viewString ->
                        onNavigateToSection(viewString)
                    }
                )
                // Optionally, add left/right navigation arrows for card switching
                if (cardsToShow.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                currentCardIndex = (currentCardIndex - 1 + cardsToShow.size) % cardsToShow.size
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text("<", color = Color.White, fontSize = 32.sp)
                        }
                        Spacer(modifier = Modifier.width(1.dp))
                        IconButton(
                            onClick = {
                                currentCardIndex = (currentCardIndex + 1) % cardsToShow.size
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text(">", color = Color.White, fontSize = 32.sp)
                        }
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        // Mini Ticker (bottom, centered)
        MiniTickerView()
        Spacer(modifier = Modifier.height(24.dp))
    }
    // Handle chart sheet presentation
    if (isChartSheetPresented) {
        // In a real implementation, you'd show a modal or navigate to charts
        // For now, we'll just close it after a delay
        LaunchedEffect(isChartSheetPresented) {
            kotlinx.coroutines.delay(100)
            isChartSheetPresented = false
        }
    }
}

// Navigation destinations enum for type safety
enum class HomeSection(val route: String) {
    PROFILE("PROFILE"),
    BOTTLES("BOTTLES"),
    SLEEP("SLEEP"),
    FOOD("FOOD"),
    MEDS("MEDS"),
    WIND("WIND"),
    POO("POO"),
    SETTINGS("SETTINGS"),
    TEST("TEST")
} 