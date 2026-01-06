package com.example.luminacal.data.repository

import android.util.Log
import com.example.luminacal.data.local.CustomFoodDao
import com.example.luminacal.data.local.CustomFoodEntity
import com.example.luminacal.data.ml.NutritionInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Repository for managing custom food entries
 */
class CustomFoodRepository(private val customFoodDao: CustomFoodDao) {
    
    companion object {
        private const val TAG = "CustomFoodRepository"
    }
    
    /**
     * All custom foods as Flow
     */
    val allCustomFoods: Flow<List<CustomFoodEntity>> = customFoodDao.getAllCustomFoods()
        .catch { e ->
            Log.e(TAG, "Error fetching custom foods", e)
            emit(emptyList())
        }
    
    /**
     * Favorite foods only
     */
    val favoriteFoods: Flow<List<CustomFoodEntity>> = customFoodDao.getFavoriteFoods()
        .catch { e ->
            Log.e(TAG, "Error fetching favorite foods", e)
            emit(emptyList())
        }
    
    /**
     * Recent foods (most recently used)
     */
    fun getRecentFoods(limit: Int = 10): Flow<List<CustomFoodEntity>> = 
        customFoodDao.getRecentFoods(limit)
            .catch { e ->
                Log.e(TAG, "Error fetching recent foods", e)
                emit(emptyList())
            }
    
    /**
     * Search custom foods by name
     */
    fun searchCustomFoods(query: String): Flow<List<CustomFoodEntity>> = 
        customFoodDao.searchByName(query)
            .catch { e ->
                Log.e(TAG, "Error searching custom foods", e)
                emit(emptyList())
            }
    
    /**
     * Get all custom foods as NutritionInfo for unified display
     */
    val allCustomFoodsAsNutritionInfo: Flow<List<NutritionInfo>> = allCustomFoods
        .map { foods -> foods.map { it.toNutritionInfo() } }
    
    /**
     * Save a new custom food
     */
    suspend fun saveCustomFood(
        name: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        servingSize: String = "1 serving"
    ): Result<Long> = runCatching {
        val entity = CustomFoodEntity(
            name = name,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            servingSize = servingSize
        )
        customFoodDao.insertFood(entity)
    }.onFailure { e ->
        Log.e(TAG, "Error saving custom food: $name", e)
    }
    
    /**
     * Update an existing custom food
     */
    suspend fun updateCustomFood(food: CustomFoodEntity): Result<Unit> = runCatching {
        customFoodDao.updateFood(food)
    }.onFailure { e ->
        Log.e(TAG, "Error updating custom food: ${food.name}", e)
    }
    
    /**
     * Delete a custom food by ID
     */
    suspend fun deleteCustomFood(id: Long): Result<Unit> = runCatching {
        customFoodDao.deleteById(id)
    }.onFailure { e ->
        Log.e(TAG, "Error deleting custom food: $id", e)
    }
    
    /**
     * Toggle favorite status
     */
    suspend fun toggleFavorite(id: Long): Result<Unit> = runCatching {
        customFoodDao.toggleFavorite(id)
    }.onFailure { e ->
        Log.e(TAG, "Error toggling favorite: $id", e)
    }
    
    /**
     * Increment use count (called when food is logged)
     */
    suspend fun incrementUseCount(id: Long, timestamp: Long = System.currentTimeMillis()): Result<Unit> = runCatching {
        customFoodDao.incrementUseCount(id, timestamp)
    }.onFailure { e ->
        Log.e(TAG, "Error incrementing use count: $id", e)
    }
    
    /**
     * Get a custom food by ID
     */
    suspend fun getById(id: Long): CustomFoodEntity? {
        return try {
            customFoodDao.getById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting food by id: $id", e)
            null
        }
    }
    
    /**
     * Delete all custom foods (for data clear)
     */
    suspend fun deleteAll(): Result<Unit> = runCatching {
        customFoodDao.deleteAll()
    }.onFailure { e ->
        Log.e(TAG, "Error deleting all custom foods", e)
    }
}
