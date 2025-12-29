package com.example.luminacal.data.repository

import android.util.Log
import com.example.luminacal.data.local.WeightDao
import com.example.luminacal.data.local.WeightEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

data class WeightEntry(
    val id: Long = 0,
    val weightKg: Float,
    val date: Long,
    val note: String? = null
)

data class WeightTrend(
    val currentWeight: Float?,
    val previousWeight: Float?,
    val weeklyChange: Float? // positive = gained, negative = lost
)

class WeightRepository(private val dao: WeightDao) {

    companion object {
        private const val TAG = "WeightRepository"
    }

    val allWeights: Flow<List<WeightEntry>> = dao.getAllWeights()
        .map { list -> list.map { it.toWeightEntry() } }
        .catch { e ->
            Log.e(TAG, "Error fetching all weights", e)
            emit(emptyList())
        }

    val latestWeight: Flow<WeightEntry?> = dao.getLatestWeight()
        .map { it?.toWeightEntry() }
        .catch { e ->
            Log.e(TAG, "Error fetching latest weight", e)
            emit(null)
        }

    val weeklyTrend: Flow<WeightTrend> = dao.getRecentWeights()
        .map { weights ->
            if (weights.isEmpty()) {
                WeightTrend(null, null, null)
            } else {
                val current = weights.firstOrNull()?.weightKg
                val weekAgo = weights.lastOrNull()?.weightKg
                WeightTrend(
                    currentWeight = current,
                    previousWeight = weekAgo,
                    weeklyChange = if (current != null && weekAgo != null) current - weekAgo else null
                )
            }
        }
        .catch { e ->
            Log.e(TAG, "Error calculating weekly trend", e)
            emit(WeightTrend(null, null, null))
        }

    suspend fun addWeight(weightKg: Float, note: String? = null): Result<Unit> {
        // Pre-insert validation
        val validation = com.example.luminacal.util.ValidationUtils.validateWeight(weightKg)
        if (!validation.isValid) {
            Log.w(TAG, "Weight validation failed: ${validation.errorMessage}")
            return Result.failure(IllegalArgumentException(validation.errorMessage))
        }
        
        return runCatching {
            dao.insertWeight(
                WeightEntity(
                    weightKg = weightKg,
                    date = System.currentTimeMillis(),
                    note = note?.trim()?.ifBlank { null }
                )
            )
        }.onFailure { e ->
            Log.e(TAG, "Error adding weight: $weightKg kg", e)
        }
    }

    suspend fun deleteWeight(entry: WeightEntry): Result<Unit> = runCatching {
        dao.deleteWeight(
            WeightEntity(
                id = entry.id,
                weightKg = entry.weightKg,
                date = entry.date,
                note = entry.note
            )
        )
    }.onFailure { e ->
        Log.e(TAG, "Error deleting weight entry: ${entry.id}", e)
    }

    suspend fun deleteAllWeights(): Result<Unit> = runCatching {
        dao.deleteAllWeights()
    }.onFailure { e ->
        Log.e(TAG, "Error clearing all weight data", e)
    }

    private fun WeightEntity.toWeightEntry() = WeightEntry(
        id = id,
        weightKg = weightKg,
        date = date,
        note = note
    )
}
