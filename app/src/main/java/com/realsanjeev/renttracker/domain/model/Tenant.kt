package com.realsanjeev.renttracker.domain.model

data class Tenant(
    val id: Long = 0,
    val name: String,
    val propertyName: String,
    val rentPay: Double,
    val paymentDate: String,
    val electricityUnitLast: Double = 0.0,
    val electricityUnitCurrent: Double = 0.0,
    val electricityRate: Double = 0.0,
    val status: TenantStatus = TenantStatus.PAID,
    val moveInDate: String = "",
    val isAdvancePaid: Boolean = true
) {
    val electricityUnitsUsed: Double
        get() = kotlin.math.max(0.0, electricityUnitCurrent - electricityUnitLast)

    val electricityCost: Double
        get() = electricityUnitsUsed * electricityRate

    val totalAmount: Double
        get() = rentPay + electricityCost

    val calculatedStatus: TenantStatus
        get() {
            if (paymentDate.isBlank()) return status
            return try {
                val dueDate = java.time.LocalDate.parse(paymentDate)
                val today = java.time.LocalDate.now()
                when {
                    today.isAfter(dueDate) -> TenantStatus.OVERDUE
                    today.isAfter(dueDate.minusDays(5)) || today.isEqual(dueDate) -> TenantStatus.PENDING
                    else -> TenantStatus.PAID
                }
            } catch (e: Exception) {
                status
            }
        }

    val initials: String
        get() {
            if (name.isBlank()) return "--"
            val parts = name.trim().split("\\s+".toRegex())
            return if (parts.size >= 2) {
                (parts[0].first() + parts[1].first().toString()).uppercase()
            } else if (name.length >= 2) {
                name.trim().take(2).uppercase()
            } else {
                name.trim().take(1).uppercase()
            }
        }
}

data class PaymentRecord(
    val id: Long = 0,
    val tenantId: Long,
    val paymentDate: String,
    val periodCovered: String,
    val rentAmount: Double,
    val electricityUnitLast: Double,
    val electricityUnitCurrent: Double,
    val electricityRate: Double,
    val electricityAmount: Double,
    val totalAmount: Double,
    val note: String = ""
)


data class DashboardSummary(
    val totalRevenue: Double = 0.0,
    val collected: Double = 0.0,
    val pending: Double = 0.0,
    val progressPercent: Int = 0,
    val tenantCount: Int = 0
)

data class UserPreferences(
    val currencySymbol: String = "रु. ",
    val numeralPreference: Int = 0,
    val calendarPreference: Int = 0,
    val defaultElectricityRate: Double = 15.0,
    val defaultDueDay: Int = 1
) {
    enum class NumeralPreference(val value: Int) {
        AUTO(0),
        ENGLISH(1),
        NEPALI(2);

        companion object {
            fun fromValue(value: Int) = entries.firstOrNull { it.value == value } ?: AUTO
        }
    }

    enum class CalendarPreference(val value: Int) {
        AD(0),
        BS(1);

        companion object {
            fun fromValue(value: Int) = entries.firstOrNull { it.value == value } ?: AD
        }
    }
}
