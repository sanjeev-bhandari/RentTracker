package com.realsanjeev.renttracker.data.mapper

import com.realsanjeev.renttracker.data.local.db.PaymentRecordEntity
import com.realsanjeev.renttracker.data.local.db.TenantEntity
import com.realsanjeev.renttracker.domain.model.PaymentRecord
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
    },
    moveInDate = moveInDate,
    isAdvancePaid = isAdvancePaid
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
    status = status.name,
    moveInDate = moveInDate,
    isAdvancePaid = isAdvancePaid
)

fun PaymentRecordEntity.toDomain() = PaymentRecord(
    id = id,
    tenantId = tenantId,
    paymentDate = paymentDate,
    periodCovered = periodCovered,
    rentAmount = rentAmount,
    electricityUnitLast = electricityUnitLast,
    electricityUnitCurrent = electricityUnitCurrent,
    electricityRate = electricityRate,
    electricityAmount = electricityAmount,
    totalAmount = totalAmount,
    note = note
)

fun PaymentRecord.toEntity() = PaymentRecordEntity(
    id = id,
    tenantId = tenantId,
    paymentDate = paymentDate,
    periodCovered = periodCovered,
    rentAmount = rentAmount,
    electricityUnitLast = electricityUnitLast,
    electricityUnitCurrent = electricityUnitCurrent,
    electricityRate = electricityRate,
    electricityAmount = electricityAmount,
    totalAmount = totalAmount,
    note = note
)

