package com.teksta.projectparent

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.teksta.projectparent.models.HomeCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Represents the entire state for the HomeScreen UI.
 * @param homeCards The final list of cards to be displayed in the pager.
 * @param topCardAverageText The string to show in the top mini card.
 * @param tickerMessage The currently displayed message in the bottom ticker.
 */
data class HomeUiState(
    val homeCards: List<HomeCard> = emptyList(),
    val topCardAverageText: String = "Need more data...",
    val tickerMessage: String = "Welcome"
)

class HomeViewModel {
    private val settings: Settings = Settings()
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeCards()
        loadTopCardData()
        startTicker()
    }

    @OptIn(ExperimentalSettingsApi::class)
    private fun loadHomeCards() {
        val allCards = createAllPossibleCards()

        // Logging to help debug if cards aren't showing
        println("--- Checking Saved Card Settings ---")
        val allSettings = settings.keys
        if (allSettings.isEmpty()) {
            println("Settings are empty. Using default values for all cards.")
        } else {
            allSettings.forEach { key ->
                if (key.startsWith("is") && key.endsWith("CardTracked")) {
                    println("Setting found -> $key: ${settings.getBoolean(key, false)}")
                }
            }
        }
        println("------------------------------------")

        val trackedCards = allCards.filter { card ->
            // Filter the list based on the saved setting for each card's tracking key
            settings.getBoolean(card.trackingSettingKey, card.toTrack)
        }

        println("Found ${trackedCards.size} cards to display.")

        _uiState.update { it.copy(homeCards = trackedCards) }
    }

    private fun loadTopCardData() {
        // In a real app, you would fetch data from a repository here.
        // For example:
        // val averageDuration = bottleRepository.getAverageBottleDuration()
        // _uiState.update { it.copy(topCardAverageText = formatDuration(averageDuration)) }
    }

    private fun startTicker() {
        viewModelScope.launch {
            val messages = listOf(
                "Welcome to Project Parent!",
                "Next bottle is due: N/A",
                "Wake windows today: N/A",
                "Average time between bottles: N/A"
            )
            var index = 0
            while (true) {
                // Cycle through the messages every 3 seconds
                _uiState.update { it.copy(tickerMessage = messages[index]) }
                delay(3000)
                index = (index + 1) % messages.size
            }
        }
    }

    /**
     * Defines the master list of all possible cards in the application.
     * The `viewString` is a platform-agnostic identifier.
     */
    private fun createAllPossibleCards(): List<HomeCard> {
        // --- Read the saved baby's name ---
        val babyName = settings.getString("babyName", "Baby") // Default to "Baby" if not set

        // --- Determine the profile card text ---
        val profileCardText = if (babyName.isNotBlank() && babyName != "Baby") {
            "$babyName's Profile"
        } else {
            "Profile" // Fallback if no name is set or it's the default
        }

        return listOf(
            HomeCard(
                colorValue = 0xFF4FD1C5, // Mint
                viewString = "PROFILE",
                presentedString = profileCardText,
                imageToDisplay = "person",
                toTrack = true, // Always shown by default
                trackingSettingKey = "isProfileCardTracked"
            ),
            HomeCard(
                colorValue = 0xFF4CAF50, // Green
                viewString = "BOTTLES",
                presentedString = "Bottles",
                imageToDisplay = "water_drop",
                toTrack = true, // Shown by default
                trackingSettingKey = "isBottlesCardTracked"
            ),
            HomeCard(
                colorValue = 0xFF5E35B1, // Indigo
                viewString = "SLEEP",
                presentedString = "Sleep",
                imageToDisplay = "bedtime",
                toTrack = false, // Hidden by default
                trackingSettingKey = "isSleepCardTracked"
            ),
            // Add other card definitions here, e.g., FOOD, MEDS, etc.
            HomeCard(
                colorValue = 0xFF616161, // Grey
                viewString = "SETTINGS",
                presentedString = "Settings",
                imageToDisplay = "settings",
                toTrack = true, // Always shown by default
                trackingSettingKey = "isSettingsCardTracked" // Even fixed cards can have a key
            )
        )
    }
}