package com.example.luminacal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getWaterEntriesForDate(date: String): Flow<List<WaterEntity>>

    @Query("SELECT SUM(amountMl) FROM water_entries WHERE date = :date")
    fun getTotalWaterForDate(date: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterEntry(entry: WaterEntity)

    @Query("DELETE FROM water_entries WHERE date = :date")
    suspend fun deleteEntriesForDate(date: String)

    @Query("DELETE FROM water_entries")
    suspend fun deleteAllWater()
}
