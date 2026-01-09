package com.example.luminacal.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.luminacal.model.BeverageType
import com.example.luminacal.model.WaterEntry

@Entity(
    tableName = "water_entries",
    indices = [Index(value = ["date"])]
)
data class WaterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long,
    val date: String, // "2025-12-25" for daily grouping
    val beverageType: String = "WATER" // Store as String for Room compatibility
) {
    fun toWaterEntry(): WaterEntry = WaterEntry(
        id = id,
        amountMl = amountMl,
        timestamp = timestamp,
        beverageType = try { 
            BeverageType.valueOf(beverageType) 
        } catch (e: Exception) { 
            BeverageType.WATER 
        }
    )

    companion object {
        fun fromWaterEntry(entry: WaterEntry, date: String): WaterEntity =
            WaterEntity(
                id = entry.id,
                amountMl = entry.amountMl,
                timestamp = entry.timestamp,
                date = date,
                beverageType = entry.beverageType.name
            )
    }
}
