package com.realsanjeev.renttracker.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tenants")
data class TenantEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val propertyName: String,
    val rentPay: Double,
    val paymentDate: String,
    val electricityUnitLast: Double,
    val electricityUnitCurrent: Double,
    val electricityRate: Double,
    val status: String
)
