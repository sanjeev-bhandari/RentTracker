package com.realsanjeev.renttracker.ui.util

import com.realsanjeev.renttracker.domain.model.UserPreferences
import java.time.LocalDate
import java.util.Locale

object Formatting {
    fun formatAmount(amount: Double, currencySymbol: String): String {
        val formatted = "$currencySymbol${String.format(Locale.US, "%,.0f", amount)}"
        return formatted
    }

    fun formatNumber(str: String, useNepaliNumerals: Boolean): String {
        if (!useNepaliNumerals) return str
        return toNepaliDigits(str)
    }

    fun formatNumber(value: Double, useNepaliNumerals: Boolean): String {
        val str = if (value == value.toLong().toDouble()) value.toLong().toString() else String.format(Locale.US, "%.1f", value)
        return formatNumber(str, useNepaliNumerals)
    }

    fun toNepaliDigits(input: String): String {
        return buildString {
            for (c in input) {
                if (c in '0'..'9') {
                    append((0x0966 + (c - '0')).toChar())
                } else {
                    append(c)
                }
            }
        }
    }

    /**
     * Formats a stored AD date string (yyyy-MM-dd) according to the user's
     * calendar preference. Returns the original string if it cannot be parsed.
     */
    fun formatDateDisplay(
        dateString: String,
        calendarPreference: Int,
        useNepaliNumerals: Boolean
    ): String {
        if (dateString.isBlank()) return dateString
        val adDate = runCatching { LocalDate.parse(dateString) }.getOrNull()
        val displayed = if (
            adDate != null &&
            calendarPreference == UserPreferences.CalendarPreference.BS.value
        ) {
            runCatching { NepaliCalendar.toBs(adDate).toString() }.getOrElse { dateString }
        } else {
            dateString
        }
        return if (useNepaliNumerals) toNepaliDigits(displayed) else displayed
    }

    fun formatAmountLocalized(amount: Double, currencySymbol: String, useNepaliNumerals: Boolean): String {
        val symbol = if (currencySymbol == "NRs. ") "रु. " else currencySymbol
        val base = "$symbol${String.format(Locale.US, "%,.0f", amount)}"
        return if (useNepaliNumerals) toNepaliDigits(base) else base
    }

    fun ordinalSuffix(day: Int): String {
        return when {
            day in 11..13 -> "${day}th"
            day % 10 == 1 -> "${day}st"
            day % 10 == 2 -> "${day}nd"
            day % 10 == 3 -> "${day}rd"
            else -> "${day}th"
        }
    }

    fun ordinalSuffixLocalized(day: Int, useNepaliNumerals: Boolean): String {
        return if (useNepaliNumerals) toNepaliDigits(day.toString()) else ordinalSuffix(day)
    }
}
