package com.example.luminacal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM scan_history ORDER BY date DESC LIMIT :limit")
    fun getRecentScans(limit: Int): Flow<List<ScanHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity)

    @Query("DELETE FROM scan_history WHERE id NOT IN (SELECT id FROM scan_history ORDER BY date DESC LIMIT :maxItems)")
    suspend fun trimHistory(maxItems: Int)

    @Query("DELETE FROM scan_history")
    suspend fun clearHistory()
}
