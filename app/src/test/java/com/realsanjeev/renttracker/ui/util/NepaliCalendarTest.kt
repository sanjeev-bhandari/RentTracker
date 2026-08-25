package com.realsanjeev.renttracker.ui.util

import com.realsanjeev.renttracker.domain.model.UserPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.LocalDate

class NepaliCalendarTest {

    @Test
    fun `epoch conversion BS 1970-01-01 equals AD 1913-04-13`() {
        assertEquals(NepaliDate(1970, 1, 1), NepaliCalendar.toBs(LocalDate.of(1913, 4, 13)))
    }

    @Test
    fun `Nepali New Year 2081 is April 13 2024`() {
        assertEquals(NepaliDate(2081, 1, 1), NepaliCalendar.toBs(LocalDate.of(2024, 4, 13)))
    }

    @Test
    fun `Nepali New Year 2080 is April 14 2023`() {
        assertEquals(NepaliDate(2080, 1, 1), NepaliCalendar.toBs(LocalDate.of(2023, 4, 14)))
    }

    @Test
    fun `BS New Year 2000 is April 14 1943`() {
        assertEquals(NepaliDate(2000, 1, 1), NepaliCalendar.toBs(LocalDate.of(1943, 4, 14)))
    }

    @Test
    fun `toAd converts BS 2081-01-01 back to April 13 2024`() {
        assertEquals(LocalDate.of(2024, 4, 13), NepaliCalendar.toAd(NepaliDate(2081, 1, 1)))
    }

    @Test
    fun `round trip preserves date across supported range`() {
        var date = LocalDate.of(1913, 4, 13)
        val end = LocalDate.of(2193, 4, 13)
        var checked = 0
        while (date.isBefore(end) && checked < 4000) {
            val bs = NepaliCalendar.toBs(date)
            assertEquals(date, NepaliCalendar.toAd(bs))
            date = date.plusMonths(1)
            checked++
        }
    }

    @Test
    fun `toBs handles dates in BS 1969 before the epoch`() {
        assertEquals(NepaliDate(1969, 1, 1), NepaliCalendar.toBs(LocalDate.of(1912, 4, 12)))
        assertEquals(NepaliDate(1969, 12, 31), NepaliCalendar.toBs(LocalDate.of(1913, 4, 12)))
    }

    @Test
    fun `toAd handles BS 1969 correctly`() {
        assertEquals(LocalDate.of(1912, 4, 12), NepaliCalendar.toAd(NepaliDate(1969, 1, 1)))
        assertEquals(LocalDate.of(1913, 4, 12), NepaliCalendar.toAd(NepaliDate(1969, 12, 31)))
    }

    @Test
    fun `toBs accepts the last supported day and rejects dates beyond`() {
        assertEquals(NepaliDate(2250, 12, 30), NepaliCalendar.toBs(LocalDate.of(2194, 4, 21)))
        assertThrows(IllegalArgumentException::class.java) {
            NepaliCalendar.toBs(LocalDate.of(2194, 4, 22))
        }
    }

    @Test
    fun `toBs rejects dates before the supported range`() {
        assertThrows(IllegalArgumentException::class.java) {
            NepaliCalendar.toBs(LocalDate.of(1912, 4, 11))
        }
    }

    @Test
    fun `days in month matches data table`() {
        assertEquals(31, NepaliCalendar.daysInMonth(2081, 1))
        assertEquals(29, NepaliCalendar.daysInMonth(2081, 11))
        assertEquals(31, NepaliCalendar.daysInMonth(2081, 12))
        assertEquals(30, NepaliCalendar.daysInMonth(2082, 9))
    }

    @Test
    fun `days in year matches sum of months`() {
        for (year in 2070..2090) {
            val monthSum = (1..12).sumOf { NepaliCalendar.daysInMonth(year, it) }
            assertEquals("Year $year", NepaliCalendar.daysInYear(year), monthSum)
        }
    }

    @Test
    fun `month names are correct`() {
        assertEquals("Baishakh", NepaliCalendar.monthName(1))
        assertEquals("Shrawan", NepaliCalendar.monthName(4))
        assertEquals("Chaitra", NepaliCalendar.monthName(12))
    }
}

class NepaliDateFormattingTest {

    @Test
    fun `formatDateDisplay shows AD date when preference is AD`() {
        val result = Formatting.formatDateDisplay(
            "2026-07-15",
            UserPreferences.CalendarPreference.AD.value,
            useNepaliNumerals = false
        )
        assertEquals("2026-07-15", result)
    }

    @Test
    fun `formatDateDisplay shows BS date when preference is BS`() {
        val result = Formatting.formatDateDisplay(
            "2024-04-13",
            UserPreferences.CalendarPreference.BS.value,
            useNepaliNumerals = false
        )
        assertEquals("2081-01-01", result)
    }

    @Test
    fun `formatDateDisplay applies Nepali digits when requested`() {
        val result = Formatting.formatDateDisplay(
            "2024-04-13",
            UserPreferences.CalendarPreference.BS.value,
            useNepaliNumerals = true
        )
        assertEquals("२०८१-०१-०१", result)
    }

    @Test
    fun `formatDateDisplay returns original string when unparseable`() {
        val result = Formatting.formatDateDisplay(
            "unknown",
            UserPreferences.CalendarPreference.BS.value,
            useNepaliNumerals = false
        )
        assertEquals("unknown", result)
    }

    @Test
    fun `formatDateDisplay handles blank string`() {
        val result = Formatting.formatDateDisplay(
            "",
            UserPreferences.CalendarPreference.BS.value,
            useNepaliNumerals = false
        )
        assertEquals("", result)
    }

    @Test
    fun `formatDateDisplay falls back to raw string for out of range dates instead of crashing`() {
        val result = Formatting.formatDateDisplay(
            "2195-01-01",
            UserPreferences.CalendarPreference.BS.value,
            useNepaliNumerals = false
        )
        assertEquals("2195-01-01", result)
    }

    @Test
    fun `formatDateDisplay falls back to raw string for pre epoch dates`() {
        val result = Formatting.formatDateDisplay(
            "1900-01-01",
            UserPreferences.CalendarPreference.BS.value,
            useNepaliNumerals = false
        )
        assertEquals("1900-01-01", result)
    }
}
