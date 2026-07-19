package com.realsanjeev.renttracker.ui.addtenant

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.model.TenantStatus
import com.realsanjeev.renttracker.domain.model.UserPreferences
import com.realsanjeev.renttracker.domain.repository.TenantRepository
import com.realsanjeev.renttracker.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class AddEditTenantUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val propertyName: String = "",
    val rentPay: String = "",
    val paymentDate: String = "",
    val electricityUnitLast: String = "",
    val electricityUnitCurrent: String = "",
    val electricityRate: String = "",
    val status: TenantStatus = TenantStatus.PAID,
    val loading: Boolean = false,
    val valid: Boolean = false
)

sealed interface AddEditTenantEvent {
    data object Saved : AddEditTenantEvent
    data object Deleted : AddEditTenantEvent
    data class Error(val message: String) : AddEditTenantEvent
}

@HiltViewModel
class AddEditTenantViewModel @Inject constructor(
    private val tenantRepository: TenantRepository,
    private val preferencesRepository: UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val tenantId: Long = savedStateHandle.get<Long>("tenantId") ?: -1L

    private val _uiState = MutableStateFlow(AddEditTenantUiState())
    val uiState: StateFlow<AddEditTenantUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddEditTenantEvent>()
    val events: SharedFlow<AddEditTenantEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            val prefs = preferencesRepository.preferences.first()
            _uiState.update {
                it.copy(
                    electricityRate = formatDouble(prefs.defaultElectricityRate),
                    paymentDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                )
            }
            if (tenantId > 0) {
                tenantRepository.observeTenantById(tenantId).first()?.let { tenant ->
                    _uiState.update {
                        it.copy(
                            isEditing = true,
                            name = tenant.name,
                            propertyName = tenant.propertyName,
                            rentPay = formatDouble(tenant.rentPay),
                            paymentDate = tenant.paymentDate,
                            electricityUnitLast = formatDouble(tenant.electricityUnitLast),
                            electricityUnitCurrent = formatDouble(tenant.electricityUnitCurrent),
                            electricityRate = formatDouble(tenant.electricityRate),
                            status = tenant.status
                        )
                    }
                }
            }
            validateForm()
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value) }
        validateForm()
    }

    fun updatePropertyName(value: String) {
        _uiState.update { it.copy(propertyName = value) }
        validateForm()
    }

    fun updateRentPay(value: String) {
        _uiState.update { it.copy(rentPay = value) }
        validateForm()
    }

    fun updatePaymentDate(value: String) {
        _uiState.update { it.copy(paymentDate = value) }
        validateForm()
    }

    fun updateElectricityUnitLast(value: String) {
        _uiState.update { it.copy(electricityUnitLast = value) }
        validateForm()
    }

    fun updateElectricityUnitCurrent(value: String) {
        _uiState.update { it.copy(electricityUnitCurrent = value) }
        validateForm()
    }

    fun updateElectricityRate(value: String) {
        _uiState.update { it.copy(electricityRate = value) }
        validateForm()
    }

    fun updateStatus(status: TenantStatus) {
        _uiState.update { it.copy(status = status) }
    }

    fun save() {
        val state = _uiState.value
        if (!state.valid) return

        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            try {
                val tenant = Tenant(
                    id = if (state.isEditing) tenantId else 0,
                    name = state.name.trim(),
                    propertyName = state.propertyName.trim(),
                    rentPay = state.rentPay.toDoubleOrNull() ?: 0.0,
                    paymentDate = state.paymentDate.trim(),
                    electricityUnitLast = state.electricityUnitLast.toDoubleOrNull() ?: 0.0,
                    electricityUnitCurrent = state.electricityUnitCurrent.toDoubleOrNull() ?: 0.0,
                    electricityRate = state.electricityRate.toDoubleOrNull() ?: 0.0,
                    status = state.status
                )
                if (state.isEditing) {
                    tenantRepository.updateTenant(tenant)
                } else {
                    tenantRepository.addTenant(tenant)
                }
                _events.emit(AddEditTenantEvent.Saved)
            } catch (e: Exception) {
                _events.emit(AddEditTenantEvent.Error(e.message ?: "Failed to save"))
            } finally {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    fun delete() {
        if (!_uiState.value.isEditing) return
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            try {
                tenantRepository.deleteTenant(tenantId)
                _events.emit(AddEditTenantEvent.Deleted)
            } catch (e: Exception) {
                _events.emit(AddEditTenantEvent.Error(e.message ?: "Failed to delete"))
            } finally {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }

    private fun validateForm() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                valid = state.name.trim().isNotBlank() &&
                        (state.rentPay.toDoubleOrNull() ?: 0.0) > 0
            )
        }
    }

    private fun formatDouble(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}
