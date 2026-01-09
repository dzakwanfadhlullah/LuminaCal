package com.example.luminacal.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAllWeights(): Flow<List<WeightEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 1")
    fun getLatestWeight(): Flow<WeightEntity?>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 7")
    fun getRecentWeights(): Flow<List<WeightEntity>>

    @Insert
    suspend fun insertWeight(weight: WeightEntity)

    @Delete
    suspend fun deleteWeight(weight: WeightEntity)

    @Query("DELETE FROM weight_entries")
    suspend fun deleteAllWeights()
    
    // Paginated queries for performance
    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getWeightsPaginated(limit: Int, offset: Int): List<WeightEntity>
    
    @Query("SELECT COUNT(*) FROM weight_entries")
    suspend fun getWeightCount(): Int
    
    // Date range query for statistics
    @Query("SELECT * FROM weight_entries WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getWeightsInRange(startDate: Long, endDate: Long): Flow<List<WeightEntity>>
}
