package com.realsanjeev.renttracker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.realsanjeev.renttracker.domain.model.UserPreferences
import com.realsanjeev.renttracker.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    fun updateCurrency(symbol: String) {
        viewModelScope.launch { preferencesRepository.updateCurrencySymbol(symbol) }
    }

    fun updateNumeralPreference(pref: Int) {
        viewModelScope.launch { preferencesRepository.updateNumeralPreference(pref) }
    }

    fun updateElectricityRate(rate: Double) {
        viewModelScope.launch { preferencesRepository.updateDefaultElectricityRate(rate) }
    }

    fun updateDueDay(day: Int) {
        viewModelScope.launch { preferencesRepository.updateDefaultDueDay(day) }
    }
}
