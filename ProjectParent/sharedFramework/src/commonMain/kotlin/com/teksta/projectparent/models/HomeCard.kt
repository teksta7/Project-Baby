package com.teksta.projectparent.models

import com.benasher44.uuid.uuid4 // From a KMP-compatible UUID library

/**
 * A platform-agnostic data class to represent a card on the home screen.
 *
 * @param id A unique identifier for the card.
 * @param colorValue The ARGB hex value of the card's background color (e.g., 0xFF4CAF50).
 * @param viewString The navigation route associated with this card.
 * @param presentedString The text to display on the card.
 * @param imageToDisplay A string key representing the icon to be shown.
 * @param toTrack The default tracking state for this card.
 * @param trackingSettingKey The key used to store the user's preference for this card
 * in multiplatform-settings.
 */
data class HomeCard(
    val id: String = uuid4().toString(),
    val colorValue: Long,
    val viewString: String,
    val presentedString: String,
    val imageToDisplay: String,
    val toTrack: Boolean,
    val trackingSettingKey: String
)