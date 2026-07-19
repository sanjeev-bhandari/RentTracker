package com.realsanjeev.renttracker.di

import com.realsanjeev.renttracker.data.repository.TenantRepositoryImpl
import com.realsanjeev.renttracker.data.repository.UserPreferencesRepositoryImpl
import com.realsanjeev.renttracker.domain.repository.TenantRepository
import com.realsanjeev.renttracker.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTenantRepository(impl: TenantRepositoryImpl): TenantRepository

    @Binds
    abstract fun bindUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository
}
