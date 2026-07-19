package com.realsanjeev.renttracker.ui.util

import java.util.Locale

object Formatting {
    fun formatAmount(amount: Double, currencySymbol: String): String {
        val formatted = "$currencySymbol${String.format(Locale.US, "%,.0f", amount)}"
        return formatted
    }

    fun formatNumber(str: String, useNepaliNumerals: Boolean): String {
        if (!useNepaliNumerals) return str
        return convertToNepaliDigits(str)
    }

    fun formatAmountLocalized(amount: Double, currencySymbol: String, useNepaliNumerals: Boolean): String {
        val symbol = if (currencySymbol == "NRs. ") "रु. " else currencySymbol
        val base = "$symbol${String.format(Locale.US, "%,.0f", amount)}"
        return if (useNepaliNumerals) convertToNepaliDigits(base) else base
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
        val suffix = ordinalSuffix(day)
        return if (useNepaliNumerals) convertToNepaliDigits(suffix) else suffix
    }

    private fun convertToNepaliDigits(input: String): String {
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
}
