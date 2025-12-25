package com.example.luminacal.data.repository

import com.example.luminacal.data.local.WaterDao
import com.example.luminacal.data.local.WaterEntity
import com.example.luminacal.model.WaterEntry
import com.example.luminacal.model.WaterState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WaterRepository(private val dao: WaterDao) {
    
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
                target = 2000,
                glassCount = entries.size
            )
        }
    }
    
    suspend fun addWater(amountMl: Int) {
        val entry = WaterEntity(
            amountMl = amountMl,
            timestamp = System.currentTimeMillis(),
            date = getTodayDate()
        )
        dao.insertWaterEntry(entry)
    }
}
