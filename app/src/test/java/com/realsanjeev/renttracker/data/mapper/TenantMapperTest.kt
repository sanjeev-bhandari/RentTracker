package com.realsanjeev.renttracker.data.mapper

import com.realsanjeev.renttracker.data.local.db.TenantEntity
import com.realsanjeev.renttracker.domain.model.TenantStatus
import org.junit.Assert.*
import org.junit.Test

class TenantMapperTest {

    @Test
    fun `entity to domain maps all fields correctly`() {
        val entity = TenantEntity(
            id = 1,
            name = "Alex Sharma",
            propertyName = "Apt 3B",
            rentPay = 2500.0,
            paymentDate = "2026-07-01",
            electricityUnitLast = 1400.0,
            electricityUnitCurrent = 1450.0,
            electricityRate = 15.0,
            status = "PAID"
        )
        val domain = entity.toDomain()

        assertEquals(1, domain.id)
        assertEquals("Alex Sharma", domain.name)
        assertEquals("Apt 3B", domain.propertyName)
        assertEquals(2500.0, domain.rentPay, 0.01)
        assertEquals("2026-07-01", domain.paymentDate)
        assertEquals(1400.0, domain.electricityUnitLast, 0.01)
        assertEquals(1450.0, domain.electricityUnitCurrent, 0.01)
        assertEquals(15.0, domain.electricityRate, 0.01)
        assertEquals(TenantStatus.PAID, domain.status)
    }

    @Test
    fun `entity to domain falls back to PENDING for unknown status`() {
        val entity = TenantEntity(
            id = 1,
            name = "Test",
            propertyName = "Test",
            rentPay = 1000.0,
            paymentDate = "2026-07-01",
            electricityUnitLast = 0.0,
            electricityUnitCurrent = 0.0,
            electricityRate = 15.0,
            status = "INVALID"
        )
        val domain = entity.toDomain()
        assertEquals(TenantStatus.PENDING, domain.status)
    }

    @Test
    fun `domain to entity maps all fields correctly`() {
        val domain = com.realsanjeev.renttracker.domain.model.Tenant(
            id = 1,
            name = "Alex Sharma",
            propertyName = "Apt 3B",
            rentPay = 2500.0,
            paymentDate = "2026-07-01",
            electricityUnitLast = 1400.0,
            electricityUnitCurrent = 1450.0,
            electricityRate = 15.0,
            status = TenantStatus.OVERDUE
        )
        val entity = domain.toEntity()

        assertEquals(1, entity.id)
        assertEquals("Alex Sharma", entity.name)
        assertEquals("Apt 3B", entity.propertyName)
        assertEquals(2500.0, entity.rentPay, 0.01)
        assertEquals("2026-07-01", entity.paymentDate)
        assertEquals(1400.0, entity.electricityUnitLast, 0.01)
        assertEquals(1450.0, entity.electricityUnitCurrent, 0.01)
        assertEquals(15.0, entity.electricityRate, 0.01)
        assertEquals("OVERDUE", entity.status)
    }

    @Test
    fun `round-trip domain to entity to domain preserves data`() {
        val domain = com.realsanjeev.renttracker.domain.model.Tenant(
            id = 42,
            name = "Priya Nair",
            propertyName = "Sunset Heights Apt 7A",
            rentPay = 2800.0,
            paymentDate = "2026-07-03",
            electricityUnitLast = 950.0,
            electricityUnitCurrent = 1010.0,
            electricityRate = 15.0,
            status = TenantStatus.PENDING
        )
        val entity = domain.toEntity()
        val roundTripped = entity.toDomain()

        assertEquals(domain, roundTripped)
    }
}
