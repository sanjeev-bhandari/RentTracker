package com.realsanjeev.renttracker.data.repository

import com.realsanjeev.renttracker.data.local.db.TenantDao
import com.realsanjeev.renttracker.data.mapper.toDomain
import com.realsanjeev.renttracker.data.mapper.toEntity
import com.realsanjeev.renttracker.domain.model.DashboardSummary
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.repository.TenantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TenantRepositoryImpl @Inject constructor(
    private val tenantDao: TenantDao
) : TenantRepository {

    override fun observeAllTenants(): Flow<List<Tenant>> =
        tenantDao.observeAllTenants().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeTenantById(id: Long): Flow<Tenant?> =
        tenantDao.observeTenantById(id).map { it?.toDomain() }

    override suspend fun addTenant(tenant: Tenant): Long =
        tenantDao.insertTenant(tenant.toEntity())

    override suspend fun updateTenant(tenant: Tenant) {
        tenantDao.updateTenant(tenant.toEntity())
    }

    override suspend fun deleteTenant(id: Long) {
        tenantDao.deleteTenant(id)
    }

    override fun observeDashboardSummary(): Flow<DashboardSummary> =
        observeAllTenants().map { tenants ->
            val collected = tenants.filter { it.status == com.realsanjeev.renttracker.domain.model.TenantStatus.PAID }
                .sumOf { it.totalAmount }
            val pending = tenants.filter { it.status != com.realsanjeev.renttracker.domain.model.TenantStatus.PAID }
                .sumOf { it.totalAmount }
            val total = collected + pending
            val progress = if (total > 0) ((collected / total) * 100).toInt() else 0

            DashboardSummary(
                totalRevenue = total,
                collected = collected,
                pending = pending,
                progressPercent = progress,
                tenantCount = tenants.size
            )
        }
}
