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
    val status: TenantStatus = TenantStatus.PAID
) {
    val electricityUnitsUsed: Double
        get() = kotlin.math.max(0.0, electricityUnitCurrent - electricityUnitLast)

    val electricityCost: Double
        get() = electricityUnitsUsed * electricityRate

    val totalAmount: Double
        get() = rentPay + electricityCost

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
}
