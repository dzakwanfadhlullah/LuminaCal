package com.example.luminacal.data.repository

import android.util.Log
import com.example.luminacal.data.local.HealthMetricsDao
import com.example.luminacal.data.local.HealthMetricsEntity
import com.example.luminacal.model.HealthMetrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class HealthMetricsRepository(private val dao: HealthMetricsDao) {
    
    companion object {
        private const val TAG = "HealthMetricsRepository"
    }
    
    val healthMetrics: Flow<HealthMetrics?> = dao.getHealthMetrics()
        .map { entity -> entity?.toHealthMetrics() }
        .catch { e ->
            Log.e(TAG, "Error fetching health metrics", e)
            emit(null)
        }

    suspend fun saveHealthMetrics(metrics: HealthMetrics): Result<Unit> = runCatching {
        dao.saveHealthMetrics(HealthMetricsEntity.fromHealthMetrics(metrics))
    }.onFailure { e ->
        Log.e(TAG, "Error saving health metrics", e)
    }
}
