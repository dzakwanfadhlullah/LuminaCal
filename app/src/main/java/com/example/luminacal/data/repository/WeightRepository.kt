package com.example.luminacal.data.repository

import com.example.luminacal.data.local.WeightDao
import com.example.luminacal.data.local.WeightEntity
import kotlinx.coroutines.flow.Flow
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

    val allWeights: Flow<List<WeightEntry>> = dao.getAllWeights().map { list ->
        list.map { it.toWeightEntry() }
    }

    val latestWeight: Flow<WeightEntry?> = dao.getLatestWeight().map { it?.toWeightEntry() }

    val weeklyTrend: Flow<WeightTrend> = dao.getRecentWeights().map { weights ->
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

    suspend fun addWeight(weightKg: Float, note: String? = null) {
        dao.insertWeight(
            WeightEntity(
                weightKg = weightKg,
                date = System.currentTimeMillis(),
                note = note
            )
        )
    }

    suspend fun deleteWeight(entry: WeightEntry) {
        dao.deleteWeight(
            WeightEntity(
                id = entry.id,
                weightKg = entry.weightKg,
                date = entry.date,
                note = entry.note
            )
        )
    }

    private fun WeightEntity.toWeightEntry() = WeightEntry(
        id = id,
        weightKg = weightKg,
        date = date,
        note = note
    )
}
