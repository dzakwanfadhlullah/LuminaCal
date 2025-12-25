package com.example.luminacal.data.repository

import com.example.luminacal.data.local.HealthMetricsDao
import com.example.luminacal.data.local.HealthMetricsEntity
import com.example.luminacal.model.HealthMetrics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HealthMetricsRepository(private val dao: HealthMetricsDao) {
    
    val healthMetrics: Flow<HealthMetrics?> = dao.getHealthMetrics().map { entity ->
        entity?.toHealthMetrics()
    }

    suspend fun saveHealthMetrics(metrics: HealthMetrics) {
        dao.saveHealthMetrics(HealthMetricsEntity.fromHealthMetrics(metrics))
    }
}
