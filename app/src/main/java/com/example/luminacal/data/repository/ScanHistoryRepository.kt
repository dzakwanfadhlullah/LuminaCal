package com.example.luminacal.data.repository

import android.util.Log
import com.example.luminacal.data.local.ScanHistoryDao
import com.example.luminacal.data.local.ScanHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class ScanHistoryRepository(private val scanHistoryDao: ScanHistoryDao) {
    
    companion object {
        private const val TAG = "ScanHistoryRepository"
        private const val MAX_HISTORY_ITEMS = 50
    }
    
    /**
     * Get recent scans for quick re-logging
     */
    fun getRecentScans(limit: Int = 10): Flow<List<ScanHistoryEntity>> {
        return scanHistoryDao.getRecentScans(limit)
            .catch { e ->
                Log.e(TAG, "Error fetching recent scans", e)
                emit(emptyList())
            }
    }

    /**
     * Insert a new scan into history and trim if necessary
     */
    suspend fun insertScan(scan: ScanHistoryEntity): Result<Unit> = runCatching {
        scanHistoryDao.insertScan(scan)
        scanHistoryDao.trimHistory(MAX_HISTORY_ITEMS)
    }.onFailure { e ->
        Log.e(TAG, "Error inserting scan into history: ${scan.foodName}", e)
    }

    /**
     * Clear all scan history
     */
    suspend fun clearHistory(): Result<Unit> = runCatching {
        scanHistoryDao.clearHistory()
    }.onFailure { e ->
        Log.e(TAG, "Error clearing scan history", e)
    }
}
