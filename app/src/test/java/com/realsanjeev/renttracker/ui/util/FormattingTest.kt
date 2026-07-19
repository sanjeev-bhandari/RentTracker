package com.realsanjeev.renttracker.ui.util

import org.junit.Assert.*
import org.junit.Test

class FormattingTest {

    @Test
    fun `formatAmount formats with currency symbol and comma grouping`() {
        val result = Formatting.formatAmount(2500.0, "रु. ")
        assertEquals("रु. 2,500", result)
    }

    @Test
    fun `formatAmount handles zero`() {
        val result = Formatting.formatAmount(0.0, "$ ")
        assertEquals("$ 0", result)
    }

    @Test
    fun `formatAmount handles large numbers`() {
        val result = Formatting.formatAmount(1000000.0, "₹ ")
        assertEquals("₹ 1,000,000", result)
    }

    @Test
    fun `ordinalSuffix returns st for first`() {
        assertEquals("1st", Formatting.ordinalSuffix(1))
    }

    @Test
    fun `ordinalSuffix returns nd for second`() {
        assertEquals("2nd", Formatting.ordinalSuffix(2))
    }

    @Test
    fun `ordinalSuffix returns rd for third`() {
        assertEquals("3rd", Formatting.ordinalSuffix(3))
    }

    @Test
    fun `ordinalSuffix returns th for fourth`() {
        assertEquals("4th", Formatting.ordinalSuffix(4))
    }

    @Test
    fun `ordinalSuffix returns th for eleventh through thirteenth`() {
        assertEquals("11th", Formatting.ordinalSuffix(11))
        assertEquals("12th", Formatting.ordinalSuffix(12))
        assertEquals("13th", Formatting.ordinalSuffix(13))
    }

    @Test
    fun `ordinalSuffix returns st for twenty-first`() {
        assertEquals("21st", Formatting.ordinalSuffix(21))
    }

    @Test
    fun `ordinalSuffix returns nd for twenty-second`() {
        assertEquals("22nd", Formatting.ordinalSuffix(22))
    }

    @Test
    fun `formatNumber converts digits to Nepali when flag is true`() {
        val result = Formatting.formatNumber("2026-07-01", true)
        assertEquals("२०२६-०७-०१", result)
    }

    @Test
    fun `formatNumber leaves digits unchanged when flag is false`() {
        val result = Formatting.formatNumber("2026-07-01", false)
        assertEquals("2026-07-01", result)
    }

    @Test
    fun `formatNumber handles empty string`() {
        val result = Formatting.formatNumber("", false)
        assertEquals("", result)
    }

    @Test
    fun `ordinalSuffixLocalized returns Nepali digits without English suffix when flag is true`() {
        assertEquals("१", Formatting.ordinalSuffixLocalized(1, true))
        assertEquals("१२", Formatting.ordinalSuffixLocalized(12, true))
        assertEquals("२१", Formatting.ordinalSuffixLocalized(21, true))
    }

    @Test
    fun `ordinalSuffixLocalized returns English suffix when flag is false`() {
        assertEquals("1st", Formatting.ordinalSuffixLocalized(1, false))
        assertEquals("12th", Formatting.ordinalSuffixLocalized(12, false))
        assertEquals("21st", Formatting.ordinalSuffixLocalized(21, false))
    }
}
