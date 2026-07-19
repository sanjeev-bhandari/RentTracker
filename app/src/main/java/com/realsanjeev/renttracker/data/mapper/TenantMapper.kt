package com.realsanjeev.renttracker.data.mapper

import com.realsanjeev.renttracker.data.local.db.TenantEntity
import com.realsanjeev.renttracker.domain.model.Tenant
import com.realsanjeev.renttracker.domain.model.TenantStatus

fun TenantEntity.toDomain() = Tenant(
    id = id,
    name = name,
    propertyName = propertyName,
    rentPay = rentPay,
    paymentDate = paymentDate,
    electricityUnitLast = electricityUnitLast,
    electricityUnitCurrent = electricityUnitCurrent,
    electricityRate = electricityRate,
    status = try {
        TenantStatus.valueOf(status)
    } catch (e: IllegalArgumentException) {
        TenantStatus.PENDING
    }
)

fun Tenant.toEntity() = TenantEntity(
    id = id,
    name = name,
    propertyName = propertyName,
    rentPay = rentPay,
    paymentDate = paymentDate,
    electricityUnitLast = electricityUnitLast,
    electricityUnitCurrent = electricityUnitCurrent,
    electricityRate = electricityRate,
    status = status.name
)
