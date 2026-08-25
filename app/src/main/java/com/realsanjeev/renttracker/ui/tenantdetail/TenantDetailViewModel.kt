package com.realsanjeev.renttracker.ui.tenantdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsanjeev.renttracker.domain.model.PaymentRecord
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.model.UserPreferences
import com.realsanjeev.renttracker.domain.repository.TenantRepository
import com.realsanjeev.renttracker.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TenantDetailUiState(
    val tenant: Tenant? = null,
    val paymentHistory: List<PaymentRecord> = emptyList(),
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true
)

@HiltViewModel
class TenantDetailViewModel @Inject constructor(
    private val tenantRepository: TenantRepository,
    private val preferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val tenantId: Long = savedStateHandle.get<Long>("tenantId") ?: -1L

    val uiState: StateFlow<TenantDetailUiState> = combine(
        tenantRepository.observeTenantById(tenantId),
        tenantRepository.getPaymentHistory(tenantId),
        preferencesRepository.preferences
    ) { tenant, history, prefs ->
        TenantDetailUiState(
            tenant = tenant,
            paymentHistory = history,
            preferences = prefs,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TenantDetailUiState()
    )

    fun recordPayment(record: PaymentRecord, updatedTenant: Tenant) {
        viewModelScope.launch {
            tenantRepository.recordPayment(record, updatedTenant)
        }
    }

    fun deleteTenant(onDeleted: () -> Unit) {
        if (tenantId <= 0) return
        viewModelScope.launch {
            tenantRepository.deleteTenant(tenantId)
            onDeleted()
        }
    }
}
