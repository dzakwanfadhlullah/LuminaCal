package com.example.luminacal.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for custom food entries
 */
@Dao
interface CustomFoodDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: CustomFoodEntity): Long
    
    @Update
    suspend fun updateFood(food: CustomFoodEntity)
    
    @Delete
    suspend fun deleteFood(food: CustomFoodEntity)
    
    @Query("DELETE FROM custom_foods WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("SELECT * FROM custom_foods ORDER BY name ASC")
    fun getAllCustomFoods(): Flow<List<CustomFoodEntity>>
    
    @Query("SELECT * FROM custom_foods ORDER BY lastUsed DESC LIMIT :limit")
    fun getRecentFoods(limit: Int): Flow<List<CustomFoodEntity>>
    
    @Query("SELECT * FROM custom_foods WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteFoods(): Flow<List<CustomFoodEntity>>
    
    @Query("SELECT * FROM custom_foods WHERE id = :id")
    suspend fun getById(id: Long): CustomFoodEntity?
    
    @Query("UPDATE custom_foods SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)
    
    @Query("UPDATE custom_foods SET useCount = useCount + 1, lastUsed = :timestamp WHERE id = :id")
    suspend fun incrementUseCount(id: Long, timestamp: Long)
    
    @Query("SELECT * FROM custom_foods WHERE name LIKE '%' || :query || '%' ORDER BY useCount DESC")
    fun searchByName(query: String): Flow<List<CustomFoodEntity>>
    
    @Query("DELETE FROM custom_foods")
    suspend fun deleteAll()
}
