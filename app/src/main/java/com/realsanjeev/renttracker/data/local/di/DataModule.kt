package com.realsanjeev.renttracker.data.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.realsanjeev.renttracker.data.local.db.TenantDao
import com.realsanjeev.renttracker.data.local.db.TenantDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TenantDatabase =
        Room.databaseBuilder(
            context,
            TenantDatabase::class.java,
            "rent_tracker.db"
        ).build()

    @Provides
    fun provideTenantDao(database: TenantDatabase): TenantDao =
        database.tenantDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("rent_tracker_prefs") }
        )
}
