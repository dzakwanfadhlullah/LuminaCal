package com.example.luminacal.data.repository

import android.util.Log
import com.example.luminacal.data.local.WaterDao
import com.example.luminacal.data.local.WaterEntity
import com.example.luminacal.model.WaterEntry
import com.example.luminacal.model.WaterState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WaterRepository(private val dao: WaterDao) {
    
    companion object {
        private const val TAG = "WaterRepository"
    }
    
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    
    fun getTodayDate(): String = LocalDate.now().format(dateFormatter)
    
    fun getWaterStateForToday(): Flow<WaterState> {
        val today = getTodayDate()
        return combine(
            dao.getTotalWaterForDate(today),
            dao.getWaterEntriesForDate(today)
        ) { total, entries ->
            WaterState(
                consumed = total ?: 0,
                target = 2000, // TODO: Move to HealthMetrics
                glassCount = entries.size
            )
        }.catch { e ->
            Log.e(TAG, "Error fetching water state for today", e)
            emit(WaterState())
        }
    }
    
    suspend fun addWater(amountMl: Int): Result<Unit> = runCatching {
        val entry = WaterEntity(
            amountMl = amountMl,
            timestamp = System.currentTimeMillis(),
            date = getTodayDate()
        )
        dao.insertWaterEntry(entry)
    }.onFailure { e ->
        Log.e(TAG, "Error adding water: $amountMl ml", e)
    }
}
