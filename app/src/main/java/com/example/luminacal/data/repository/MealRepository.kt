package com.example.luminacal.data.repository

import android.util.Log
import com.example.luminacal.data.local.MealDao
import com.example.luminacal.data.local.MealEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

class MealRepository(private val mealDao: MealDao) {
    
    companion object {
        private const val TAG = "MealRepository"
    }
    
    val allMeals: Flow<List<MealEntity>> = mealDao.getAllMeals()
        .catch { e ->
            Log.e(TAG, "Error fetching all meals", e)
            emit(emptyList())
        }

    fun getMealsForDay(startOfDay: Long, endOfDay: Long): Flow<List<MealEntity>> {
        return mealDao.getMealsForDay(startOfDay, endOfDay)
            .catch { e ->
                Log.e(TAG, "Error fetching meals for day", e)
                emit(emptyList())
            }
    }

    suspend fun insertMeal(meal: MealEntity): Result<Unit> {
        // Pre-insert validation
        val nameValidation = com.example.luminacal.util.ValidationUtils.validateFoodName(meal.name)
        if (!nameValidation.isValid) {
            Log.w(TAG, "Meal validation failed: ${nameValidation.errorMessage}")
            return Result.failure(IllegalArgumentException(nameValidation.errorMessage))
        }
        
        val caloriesValidation = com.example.luminacal.util.ValidationUtils.validateCalories(meal.calories)
        if (!caloriesValidation.isValid) {
            Log.w(TAG, "Calories validation failed: ${caloriesValidation.errorMessage}")
            return Result.failure(IllegalArgumentException(caloriesValidation.errorMessage))
        }
        
        return runCatching {
            mealDao.insertMeal(meal)
        }.onFailure { e ->
            Log.e(TAG, "Error inserting meal: ${meal.name}", e)
        }
    }

    suspend fun deleteMeal(meal: MealEntity): Result<Unit> = runCatching {
        mealDao.deleteMeal(meal)
    }.onFailure { e ->
        Log.e(TAG, "Error deleting meal: ${meal.name}", e)
    }

    suspend fun deleteAllMeals(): Result<Unit> = runCatching {
        mealDao.deleteAllMeals()
    }.onFailure { e ->
        Log.e(TAG, "Error clearing all meal data", e)
    }
    
    /**
     * Calculate logging streak - consecutive days with at least 1 meal logged
     * Counts backwards from today
     */
    suspend fun getLoggingStreak(): Int {
        return try {
            val meals = mealDao.getAllMealsList()
            if (meals.isEmpty()) return 0
            
            val calendar = java.util.Calendar.getInstance()
            val today = calendar.get(java.util.Calendar.DAY_OF_YEAR)
            val year = calendar.get(java.util.Calendar.YEAR)
            
            // Get unique days with meals
            val daysWithMeals = meals.map { meal ->
                val mealCal = java.util.Calendar.getInstance()
                mealCal.timeInMillis = meal.date
                Pair(mealCal.get(java.util.Calendar.YEAR), mealCal.get(java.util.Calendar.DAY_OF_YEAR))
            }.toSet()
            
            var streak = 0
            var checkYear = year
            var checkDay = today
            
            while (daysWithMeals.contains(Pair(checkYear, checkDay))) {
                streak++
                checkDay--
                if (checkDay < 1) {
                    checkYear--
                    val tempCal = java.util.Calendar.getInstance()
                    tempCal.set(java.util.Calendar.YEAR, checkYear)
                    checkDay = tempCal.getActualMaximum(java.util.Calendar.DAY_OF_YEAR)
                }
            }
            streak
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating streak", e)
            0
        }
    }
}
