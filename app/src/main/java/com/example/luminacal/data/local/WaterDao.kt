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
    
    // Beverage type queries
    @Query("SELECT COUNT(*) FROM water_entries WHERE date = :date AND beverageType = 'COFFEE'")
    fun getCoffeeCountForDate(date: String): Flow<Int>
    
    @Query("SELECT beverageType, SUM(amountMl) as total FROM water_entries WHERE date = :date GROUP BY beverageType")
    fun getWaterBreakdownByType(date: String): Flow<List<BeverageBreakdown>>
    
    @Query("SELECT SUM(amountMl) FROM water_entries WHERE date = :date AND beverageType = :type")
    fun getTotalByBeverageType(date: String, type: String): Flow<Int?>
}

/**
 * Data class for beverage breakdown query result
 */
data class BeverageBreakdown(
    val beverageType: String,
    val total: Int
)
