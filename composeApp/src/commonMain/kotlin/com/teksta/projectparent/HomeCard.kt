package com.teksta.projectparent

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import com.russhwolf.settings.Settings

data class HomeCard(
    val id: String = java.util.UUID.randomUUID().toString(),
    val color: Color,
    val viewString: String, // TO BE USED TO CHECK WHICH VIEW TO DISPLAY ON THE CARD
    val presentedString: String, // TO BE USED TO CHECK WHICH VIEW TO DISPLAY ON THE CARD
    val imageToDisplay: String, // TO BE USED TO CHECK WHICH VIEW TO DISPLAY ON THE CARD
    val toTrack: Boolean
)

class HomeCardStore {
    private val settings: Settings = Settings()
    
    val profileHomeCard: HomeCard = HomeCard(
        color = Color(0xFF40E0D0), // mint
        viewString = "PROFILE",
        presentedString = "Baby's Profile", // Will be updated with actual profile name
        imageToDisplay = "figure.child",
        toTrack = true
    )
    
    val bottlesHomeCard: HomeCard = HomeCard(
        color = Color(0xFF4CAF50), // green
        viewString = "BOTTLES",
        presentedString = "Bottles",
        imageToDisplay = "waterbottle",
        toTrack = settings.getBoolean("com.projectparent.isBottlesCardTracked", true)
    )
    
    val sleepHomeCard: HomeCard = HomeCard(
        color = Color(0xFF3F51B5), // indigo
        viewString = "SLEEP",
        presentedString = "Sleep",
        imageToDisplay = "powersleep",
        toTrack = settings.getBoolean("com.projectparent.isSleepCardTracked", false)
    )
    
    val foodHomeCard: HomeCard = HomeCard(
        color = Color(0xFFFFEB3B), // yellow
        viewString = "FOOD",
        presentedString = "Food",
        imageToDisplay = "carrot",
        toTrack = settings.getBoolean("com.projectparent.isFoodCardTracked", false)
    )
    
    val medsHomeCard: HomeCard = HomeCard(
        color = Color(0xFFF44336), // red
        viewString = "MEDS",
        presentedString = "Medicine",
        imageToDisplay = "pill",
        toTrack = settings.getBoolean("com.projectparent.isMedsCardTracked", false)
    )
    
    val windHomeCard: HomeCard = HomeCard(
        color = Color(0xFF2196F3), // blue
        viewString = "WIND",
        presentedString = "Wind",
        imageToDisplay = "wind",
        toTrack = settings.getBoolean("com.projectparent.isWindCardTracked", false)
    )
    
    val pooHomeCard: HomeCard = HomeCard(
        color = Color(0xFF795548), // brown
        viewString = "POO",
        presentedString = "Nappies",
        imageToDisplay = "toilet",
        toTrack = settings.getBoolean("com.projectparent.isPooCardTracked", false)
    )
    
    val settingsHomeCard: HomeCard = HomeCard(
        color = Color(0xFF9E9E9E), // gray
        viewString = "SETTINGS",
        presentedString = "Settings",
        imageToDisplay = "gear",
        toTrack = true
    )
    
    val testHomeCard: HomeCard = HomeCard(
        color = Color(0xFF9C27B0), // purple
        viewString = "TEST",
        presentedString = "Test",
        imageToDisplay = "testtube.2",
        toTrack = true
    )
    
    val homeCards: List<HomeCard> = listOf(
        profileHomeCard,
        bottlesHomeCard,
        sleepHomeCard,
        foodHomeCard,
        medsHomeCard,
        windHomeCard,
        pooHomeCard,
        settingsHomeCard,
        testHomeCard
    )
}

// Extension function to get zIndex for cards
fun List<HomeCard>.zIndex(homeCard: HomeCard): Float {
    val index = indexOfFirst { it.id == homeCard.id }
    return if (index != -1) {
        (size - index).toFloat()
    } else {
        0f
    }
}

// Global instance
val HomeCards: List<HomeCard> = HomeCardStore().homeCards 