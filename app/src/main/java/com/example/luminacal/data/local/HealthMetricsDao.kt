package com.example.luminacal.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthMetricsDao {
    @Query("SELECT * FROM health_metrics WHERE id = 1")
    fun getHealthMetrics(): Flow<HealthMetricsEntity?>

    @Upsert
    suspend fun saveHealthMetrics(metrics: HealthMetricsEntity)
}
