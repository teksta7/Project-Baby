package com.teksta.projectparent.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.teksta.projectparent.HomeViewModel
import com.teksta.projectparent.models.HomeCard
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (route: String) -> Unit,
    onNavigateToCharts: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.homeCards.size })

    Scaffold(
        modifier = Modifier.statusBarsPadding(), // Handles status bar overlap
        topBar = { TopAppBar(title = { Text("Project Parent") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            TopMiniCard(
                averageText = uiState.topCardAverageText,
                onClick = onNavigateToCharts
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.homeCards.isNotEmpty()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 64.dp) // Creates space for side cards
                ) { page ->
                    val card = uiState.homeCards[page]
                    HomeCard(
                        modifier = Modifier.graphicsLayer {
                            // Replicates the SwiftUI visualEffect
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue

                            val scale = 1f - (pageOffset * 0.1f)
                            scaleX = scale
                            scaleY = scale

                            val rotation = pageOffset * 5f
                            rotationY = if (pagerState.currentPage > page) rotation else -rotation

                            alpha = 1f - (pageOffset * 0.3f)
                        },
                        homeCard = card,
                        onClick = { onNavigate(card.viewString) }
                    )
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Cards to Display",
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please enable cards in the onboarding or settings screens.",
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            MiniTicker(message = uiState.tickerMessage)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TopMiniCard(averageText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Average bottle feed time:", style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(8.dp))
            Text(averageText, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Press here for charts", style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
fun HomeCard(modifier: Modifier = Modifier, homeCard: HomeCard, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight(0.9f)
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(homeCard.colorValue))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = mapIcon(homeCard.imageToDisplay),
                contentDescription = homeCard.presentedString,
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = homeCard.presentedString,
                style = MaterialTheme.typography.h5,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Composable
fun MiniTicker(message: String) {
    Card(
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                tint = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(message)
        }
    }
}

@Composable
private fun mapIcon(iconName: String): ImageVector {
    return when (iconName) {
        "person" -> Icons.Default.Person
        "water_drop" -> Icons.Default.WaterDrop
        "bedtime" -> Icons.Default.Bedtime
        "settings" -> Icons.Default.Settings
        else -> Icons.Default.Help // A fallback icon
    }
}