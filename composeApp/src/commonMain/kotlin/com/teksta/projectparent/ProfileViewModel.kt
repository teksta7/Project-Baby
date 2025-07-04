package com.teksta.projectparent

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import kotlinx.datetime.*

class ProfileViewModel {
    private val settings = Settings()

    var babyName by mutableStateOf(settings.getStringOrNull("baby_name") ?: "")
    var birthDate by mutableStateOf(settings.getLongOrNull("baby_birthdate") ?: 0L)
    var currentWeightKg by mutableStateOf(settings.getDoubleOrNull("baby_weight_kg") ?: 0.0)
    var profileImageUri by mutableStateOf(settings.getStringOrNull("baby_profile_image_uri"))

    var ageSelector by mutableStateOf(0)
    var weightSelector by mutableStateOf(0)

    // UI Labels
    var ageLabel by mutableStateOf("")
    var ageLabelWords by mutableStateOf("")
    var ageSubLabel by mutableStateOf("")
    var weightLabel by mutableStateOf("")
    var weightLabelWords by mutableStateOf("")
    var weightSubLabel by mutableStateOf("")

    init {
        updateAgeLabels()
        updateWeightLabels()
    }

    fun updateBabyName(value: String) {
        babyName = value
        settings.putString("baby_name", value)
    }
    fun updateBirthDate(value: Long) {
        birthDate = value
        settings.putLong("baby_birthdate", value)
        updateAgeLabels()
    }
    fun updateCurrentWeightKg(value: Double) {
        currentWeightKg = value
        settings.putDouble("baby_weight_kg", value)
        updateWeightLabels()
    }
    fun updateProfileImageUri(value: String?) {
        profileImageUri = value
        if (value != null) settings.putString("baby_profile_image_uri", value)
    }

    fun onAgeCardTap() {
        ageSelector = (ageSelector + 1) % 4
        updateAgeLabels()
    }

    fun onWeightCardTap() {
        weightSelector = (weightSelector + 1) % 2
        updateWeightLabels()
    }

    private fun updateAgeLabels() {
        val now = Clock.System.now().epochSeconds
        val birth = birthDate
        val diff = now - birth
        val birthInstant = Instant.fromEpochSeconds(birth)
        val nowInstant = Instant.fromEpochSeconds(now)
        val birthDateTime = birthInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val nowDateTime = nowInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        when (ageSelector) {
            0 -> {
                val days = (now - birth) / (60 * 60 * 24)
                ageLabel = days.toString()
                ageLabelWords = "Days old"
                ageSubLabel = "Tap for weeks"
            }
            1 -> {
                val weeks = (now - birth) / (60 * 60 * 24 * 7)
                ageLabel = weeks.toString()
                ageLabelWords = "Weeks old"
                ageSubLabel = "Tap for months"
            }
            2 -> {
                val months = monthsBetween(birthDateTime, nowDateTime)
                ageLabel = months.toString()
                ageLabelWords = "Months old"
                ageSubLabel = "Tap for years"
            }
            3 -> {
                val years = nowDateTime.year - birthDateTime.year
                ageLabel = years.toString()
                ageLabelWords = "Years old"
                ageSubLabel = "Tap for days"
            }
        }
    }

    private fun updateWeightLabels() {
        when (weightSelector) {
            0 -> {
                weightLabel = String.format("%.2f", currentWeightKg)
                weightLabelWords = "kg"
                weightSubLabel = "Tap for lbs/oz"
            }
            1 -> {
                val (lbs, oz) = kgToLbsOz(currentWeightKg)
                weightLabel = "$lbs lbs ${oz.toInt()} oz"
                weightLabelWords = "lbs"
                weightSubLabel = "Tap for kg"
            }
        }
    }

    // Helper functions
    private fun kgToLbsOz(kg: Double): Pair<Int, Double> {
        val totalPounds = kg * 2.20462
        val pounds = totalPounds.toInt()
        val ounces = (totalPounds - pounds) * 16
        return pounds to ounces
    }

    private fun monthsBetween(start: LocalDate, end: LocalDate): Int {
        return (end.year - start.year) * 12 + (end.monthNumber - start.monthNumber)
    }
} 