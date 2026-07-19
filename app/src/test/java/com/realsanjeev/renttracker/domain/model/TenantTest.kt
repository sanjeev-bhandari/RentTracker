package com.realsanjeev.renttracker.domain.model

import org.junit.Assert.*
import org.junit.Test

class TenantTest {

    @Test
    fun `tenant initials from full name`() {
        val tenant = Tenant(
            name = "Alex Sharma",
            propertyName = "Apt 3B",
            rentPay = 2500.0,
            paymentDate = "2026-07-01"
        )
        assertEquals("AS", tenant.initials)
    }

    @Test
    fun `tenant initials from single name`() {
        val tenant = Tenant(
            name = "Alex",
            propertyName = "Apt 3B",
            rentPay = 2500.0,
            paymentDate = "2026-07-01"
        )
        assertEquals("AL", tenant.initials)
    }

    @Test
    fun `tenant initials from blank name`() {
        val tenant = Tenant(
            name = "",
            propertyName = "Apt 3B",
            rentPay = 2500.0,
            paymentDate = "2026-07-01"
        )
        assertEquals("--", tenant.initials)
    }

    @Test
    fun `tenant initials from single character name`() {
        val tenant = Tenant(
            name = "A",
            propertyName = "Apt 3B",
            rentPay = 2500.0,
            paymentDate = "2026-07-01"
        )
        assertEquals("A", tenant.initials)
    }

    @Test
    fun `electricity units used is current minus last`() {
        val tenant = Tenant(
            name = "Test",
            propertyName = "Test",
            rentPay = 1000.0,
            paymentDate = "2026-07-01",
            electricityUnitLast = 100.0,
            electricityUnitCurrent = 150.0
        )
        assertEquals(50.0, tenant.electricityUnitsUsed, 0.01)
    }

    @Test
    fun `electricity units used clamps to zero when current less than last`() {
        val tenant = Tenant(
            name = "Test",
            propertyName = "Test",
            rentPay = 1000.0,
            paymentDate = "2026-07-01",
            electricityUnitLast = 200.0,
            electricityUnitCurrent = 150.0
        )
        assertEquals(0.0, tenant.electricityUnitsUsed, 0.01)
    }

    @Test
    fun `total amount is rent plus electricity cost`() {
        val tenant = Tenant(
            name = "Test",
            propertyName = "Test",
            rentPay = 2500.0,
            paymentDate = "2026-07-01",
            electricityUnitLast = 100.0,
            electricityUnitCurrent = 150.0,
            electricityRate = 15.0
        )
        assertEquals(3250.0, tenant.totalAmount, 0.01)
    }

    @Test
    fun `electricity cost is units times rate`() {
        val tenant = Tenant(
            name = "Test",
            propertyName = "Test",
            rentPay = 1000.0,
            paymentDate = "2026-07-01",
            electricityUnitLast = 1400.0,
            electricityUnitCurrent = 1450.0,
            electricityRate = 15.0
        )
        assertEquals(750.0, tenant.electricityCost, 0.01)
    }

    @Test
    fun `default status is PAID`() {
        val tenant = Tenant(
            name = "Test",
            propertyName = "Test",
            rentPay = 1000.0,
            paymentDate = "2026-07-01"
        )
        assertEquals(TenantStatus.PAID, tenant.status)
    }
}
