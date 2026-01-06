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

/**
 * Weight statistics for the statistics screen
 */
data class WeightStats(
    val weeklyAverage: Float?,
    val monthlyAverage: Float?,
    val minWeight: Float?,
    val maxWeight: Float?,
    val totalChange: Float?, // from first to last entry
    val milestone: WeightMilestone? // active milestone if any
)

/**
 * Weight milestones for celebrations
 */
data class WeightMilestone(
    val type: MilestoneType,
    val kilosLost: Float
)

enum class MilestoneType(val label: String, val emoji: String) {
    ONE_KG("1 kg lost!", "🎉"),
    THREE_KG("3 kg lost!", "🌟"),
    FIVE_KG("5 kg lost!", "🔥"),
    TEN_KG("10 kg lost!", "🏆"),
    FIFTEEN_KG("15 kg lost!", "💪"),
    TWENTY_KG("20 kg lost!", "👑")
}

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
    
    /**
     * Weight statistics including weekly/monthly average, min/max, and milestones
     */
    val weightStats: Flow<WeightStats> = dao.getAllWeights()
        .map { weights ->
            if (weights.isEmpty()) {
                WeightStats(null, null, null, null, null, null)
            } else {
                val now = System.currentTimeMillis()
                val oneWeekAgo = now - (7 * 24 * 60 * 60 * 1000L)
                val oneMonthAgo = now - (30 * 24 * 60 * 60 * 1000L)
                
                // Calculate weekly average
                val weeklyWeights = weights.filter { it.date >= oneWeekAgo }
                val weeklyAvg = if (weeklyWeights.isNotEmpty()) 
                    weeklyWeights.map { it.weightKg }.average().toFloat() 
                else null
                
                // Calculate monthly average
                val monthlyWeights = weights.filter { it.date >= oneMonthAgo }
                val monthlyAvg = if (monthlyWeights.isNotEmpty()) 
                    monthlyWeights.map { it.weightKg }.average().toFloat() 
                else null
                
                // Min/Max
                val allWeightValues = weights.map { it.weightKg }
                val minW = allWeightValues.minOrNull()
                val maxW = allWeightValues.maxOrNull()
                
                // Total change (first recorded to latest)
                val oldest = weights.lastOrNull()?.weightKg
                val newest = weights.firstOrNull()?.weightKg
                val totalChange = if (oldest != null && newest != null) newest - oldest else null
                
                // Milestone detection
                val milestone = detectMilestone(oldest, newest)
                
                WeightStats(
                    weeklyAverage = weeklyAvg,
                    monthlyAverage = monthlyAvg,
                    minWeight = minW,
                    maxWeight = maxW,
                    totalChange = totalChange,
                    milestone = milestone
                )
            }
        }
        .catch { e ->
            Log.e(TAG, "Error calculating weight stats", e)
            emit(WeightStats(null, null, null, null, null, null))
        }
    
    private fun detectMilestone(startWeight: Float?, currentWeight: Float?): WeightMilestone? {
        if (startWeight == null || currentWeight == null) return null
        val lost = startWeight - currentWeight
        if (lost <= 0) return null
        
        return when {
            lost >= 20 -> WeightMilestone(MilestoneType.TWENTY_KG, lost)
            lost >= 15 -> WeightMilestone(MilestoneType.FIFTEEN_KG, lost)
            lost >= 10 -> WeightMilestone(MilestoneType.TEN_KG, lost)
            lost >= 5 -> WeightMilestone(MilestoneType.FIVE_KG, lost)
            lost >= 3 -> WeightMilestone(MilestoneType.THREE_KG, lost)
            lost >= 1 -> WeightMilestone(MilestoneType.ONE_KG, lost)
            else -> null
        }
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
