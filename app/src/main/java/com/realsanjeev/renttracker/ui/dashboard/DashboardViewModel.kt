package com.realsanjeev.renttracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsanjeev.renttracker.domain.model.DashboardSummary
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

data class DashboardUiState(
    val tenants: List<Tenant> = emptyList(),
    val summary: DashboardSummary = DashboardSummary(),
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tenantRepository: TenantRepository,
    preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        tenantRepository.observeAllTenants(),
        tenantRepository.observeDashboardSummary(),
        preferencesRepository.preferences
    ) { tenants, summary, prefs ->
        DashboardUiState(
            tenants = tenants,
            summary = summary,
            preferences = prefs,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun deleteTenant(tenant: Tenant) {
        viewModelScope.launch {
            tenantRepository.deleteTenant(tenant.id)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            uiState.value.tenants.forEach { tenant ->
                tenantRepository.deleteTenant(tenant.id)
            }
        }
    }
}
