package com.teksta.projectparent.models

import com.benasher44.uuid.uuid4 // For generating unique IDs
import kotlinx.datetime.Instant // For specific points in time (startTime, endTime)
import kotlinx.datetime.LocalDate // For the calendar date of the feed

/**
 * Represents a single bottle feed record.
 * This data class is platform-agnostic and used in shared code.
 *
 * @param id Unique identifier for the bottle feed.
 * @param date The calendar date on which the feed occurred.
 * @param startTime The exact moment the feed started.
 * @param endTime The exact moment the feed ended.
 * @param durationSeconds The total duration of the feed in seconds.
 * @param ounces The amount of milk consumed in ounces.
 * @param additionalNotes Any optional notes about the feed.
 */
data class Bottle(
    val id: String = uuid4().toString(),
    val date: LocalDate, // e.g., The feed was on June 4th, 2025
    val startTime: Instant,
    val endTime: Instant,
    val durationSeconds: Int,
    val ounces: Double,
    val additionalNotes: String? // Nullable if notes are optional
)