package com.realsanjeev.renttracker.data.repository

import com.realsanjeev.renttracker.data.local.datastore.UserPreferencesDataSource
import com.realsanjeev.renttracker.domain.model.UserPreferences
import com.realsanjeev.renttracker.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataSource: UserPreferencesDataSource
) : UserPreferencesRepository {

    override val preferences: Flow<UserPreferences> = dataSource.preferences

    override suspend fun updateCurrencySymbol(symbol: String) =
        dataSource.updateCurrencySymbol(symbol)

    override suspend fun updateNumeralPreference(preference: Int) =
        dataSource.updateNumeralPreference(preference)

    override suspend fun updateCalendarPreference(preference: Int) =
        dataSource.updateCalendarPreference(preference)

    override suspend fun updateDefaultElectricityRate(rate: Double) =
        dataSource.updateDefaultElectricityRate(rate)

    override suspend fun updateDefaultDueDay(day: Int) =
        dataSource.updateDefaultDueDay(day)
}
