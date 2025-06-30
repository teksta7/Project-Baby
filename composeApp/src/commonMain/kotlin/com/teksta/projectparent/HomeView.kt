package com.teksta.projectparent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.absoluteValue
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import androidx.compose.ui.platform.LocalConfiguration

val HomeCardWidth = 260.dp // Shared width for cards and tickers

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    bottleFeedViewModel: BottleFeedViewModel? = null,
    onNavigateToSection: (String) -> Unit = {},
    onShowCharts: () -> Unit = {}
) {
    // Retrieve baby's name from multiplatform-settings
    val settings = remember { Settings() }
    val babyName = remember { settings.getStringOrNull("baby_name") }
    val cardsToShow = remember {
        HomeCards.map { card ->
            if (card.viewString == "PROFILE" && !babyName.isNullOrBlank()) {
                card.copy(presentedString = "${babyName}'s Profile")
            } else card
        }.filter { it.toTrack }
    }
    val pagerState = rememberPagerState(pageCount = { cardsToShow.size })

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Project Parent",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TopMiniCardView(
                    average = bottleFeedViewModel?.analytics?.averageBottleDuration ?: "N/A",
                    topText = "Average bottle feed time:",
                    onTap = onShowCharts,
                    modifier = Modifier.width(HomeCardWidth)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (cardsToShow.isNotEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = ((LocalConfiguration.current.screenWidthDp.dp - HomeCardWidth) / 2).coerceAtLeast(0.dp))
                    ) { page ->
                        val card = cardsToShow[page]
                        HomeCardView(
                            modifier = Modifier
                                .graphicsLayer {
                                    val pageOffset = (
                                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                    ).absoluteValue
                                    val scale = 1f - (pageOffset * 0.15f)
                                    scaleX = scale
                                    scaleY = scale
                                    val rotation = pageOffset * 5f
                                    rotationY = if (pagerState.currentPage > page) rotation else -rotation
                                    alpha = 1f - (pageOffset * 0.3f)
                                }
                                .width(HomeCardWidth),
                            homeCard = card,
                            onCardClick = { onNavigateToSection(card.viewString) }
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Cards to Display",
                        fontSize = 20.sp,
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
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                MiniTickerView(bottleFeedViewModel = bottleFeedViewModel)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// HomeCardView now accepts a modifier for pager effects
@Composable
fun HomeCardView(
    modifier: Modifier = Modifier,
    homeCard: HomeCard,
    onCardClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val cardHeight = screenHeight * 0.5f
    Box(
        modifier = modifier
            .height(cardHeight)
            .clip(RoundedCornerShape(20.dp))
            .background(homeCard.color)
            .clickable(onClick = onCardClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Use emoji for all icons, including settings
            val iconText = when (homeCard.imageToDisplay) {
                "figure.child" -> "👶"
                "waterbottle" -> "🍼"
                "powersleep" -> "😴"
                "carrot" -> "🥕"
                "pill" -> "💊"
                "wind" -> "💨"
                "toilet" -> "🚽"
                "gear" -> "⚙️"
                "testtube.2" -> "🧪"
                else -> "📱"
            }
            Text(
                text = iconText,
                fontSize = 80.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = homeCard.presentedString,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
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