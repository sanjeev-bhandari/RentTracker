package com.realsanjeev.renttracker.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TenantEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TenantDatabase : RoomDatabase() {
    abstract fun tenantDao(): TenantDao
}
