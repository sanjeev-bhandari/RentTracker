package com.realsanjeev.renttracker.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TenantEntity::class, PaymentRecordEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TenantDatabase : RoomDatabase() {
    abstract fun tenantDao(): TenantDao
    abstract fun paymentRecordDao(): PaymentRecordDao
}
