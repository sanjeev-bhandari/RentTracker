package com.realsanjeev.renttracker.domain.repository

import com.realsanjeev.renttracker.domain.model.DashboardSummary
import com.realsanjeev.renttracker.domain.model.PaymentRecord
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface TenantRepository {
    fun observeAllTenants(): Flow<List<Tenant>>
    fun observeTenantById(id: Long): Flow<Tenant?>
    suspend fun addTenant(tenant: Tenant): Long
    suspend fun updateTenant(tenant: Tenant)
    suspend fun deleteTenant(id: Long)
    fun observeDashboardSummary(): Flow<DashboardSummary>
    suspend fun recordPayment(paymentRecord: PaymentRecord, updatedTenant: Tenant)
    fun getPaymentHistory(tenantId: Long): Flow<List<PaymentRecord>>
}

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun updateCurrencySymbol(symbol: String)
    suspend fun updateNumeralPreference(preference: Int)
    suspend fun updateCalendarPreference(preference: Int)
    suspend fun updateDefaultElectricityRate(rate: Double)
    suspend fun updateDefaultDueDay(day: Int)
}
