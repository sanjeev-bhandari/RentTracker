package com.realsanjeev.renttracker.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.realsanjeev.renttracker.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        val NUMERAL_PREFERENCE = intPreferencesKey("numeral_preference")
        val DEFAULT_ELECTRICITY_RATE = doublePreferencesKey("default_electricity_rate")
        val DEFAULT_DUE_DAY = intPreferencesKey("default_due_day")
    }

    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            currencySymbol = prefs[Keys.CURRENCY_SYMBOL] ?: "रु. ",
            numeralPreference = prefs[Keys.NUMERAL_PREFERENCE] ?: 0,
            defaultElectricityRate = prefs[Keys.DEFAULT_ELECTRICITY_RATE] ?: 15.0,
            defaultDueDay = prefs[Keys.DEFAULT_DUE_DAY] ?: 1
        )
    }

    suspend fun updateCurrencySymbol(symbol: String) {
        dataStore.edit { it[Keys.CURRENCY_SYMBOL] = symbol }
    }

    suspend fun updateNumeralPreference(preference: Int) {
        dataStore.edit { it[Keys.NUMERAL_PREFERENCE] = preference }
    }

    suspend fun updateDefaultElectricityRate(rate: Double) {
        dataStore.edit { it[Keys.DEFAULT_ELECTRICITY_RATE] = rate }
    }

    suspend fun updateDefaultDueDay(day: Int) {
        dataStore.edit { it[Keys.DEFAULT_DUE_DAY] = day }
    }
}
