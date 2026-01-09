package com.example.luminacal.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal_entries ORDER BY date DESC")
    fun getAllMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meal_entries WHERE date >= :startOfDay AND date <= :endOfDay ORDER BY date DESC")
    fun getMealsForDay(startOfDay: Long, endOfDay: Long): Flow<List<MealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meal_entries")
    suspend fun deleteAllMeals()
    
    @Query("SELECT * FROM meal_entries ORDER BY date DESC")
    suspend fun getAllMealsList(): List<MealEntity>
    
    // Paginated queries for performance
    @Query("SELECT * FROM meal_entries ORDER BY date DESC LIMIT :limit")
    fun getRecentMeals(limit: Int = 10): Flow<List<MealEntity>>
    
    @Query("SELECT * FROM meal_entries ORDER BY date DESC LIMIT :limit OFFSET :offset")
    suspend fun getMealsPaginated(limit: Int, offset: Int): List<MealEntity>
    
    @Query("SELECT COUNT(*) FROM meal_entries")
    suspend fun getMealCount(): Int
    
    // Date range query for statistics
    @Query("SELECT * FROM meal_entries WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getMealsInRange(startDate: Long, endDate: Long): Flow<List<MealEntity>>
}
