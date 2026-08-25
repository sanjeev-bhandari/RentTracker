package com.realsanjeev.renttracker.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_records")
data class PaymentRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tenantId: Long,
    val paymentDate: String,
    val periodCovered: String,
    val rentAmount: Double,
    val electricityUnitLast: Double,
    val electricityUnitCurrent: Double,
    val electricityRate: Double,
    val electricityAmount: Double,
    val totalAmount: Double,
    val note: String = ""
)
