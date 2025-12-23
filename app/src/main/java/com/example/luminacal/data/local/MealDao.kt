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
}
