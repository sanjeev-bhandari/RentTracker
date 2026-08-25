package com.realsanjeev.renttracker.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentRecordDao {
    @Query("SELECT * FROM payment_records WHERE tenantId = :tenantId ORDER BY id DESC")
    fun observeRecordsForTenant(tenantId: Long): Flow<List<PaymentRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: PaymentRecordEntity): Long

    @Query("DELETE FROM payment_records WHERE tenantId = :tenantId")
    suspend fun deleteRecordsForTenant(tenantId: Long)
}
