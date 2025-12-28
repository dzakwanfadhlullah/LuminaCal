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

    suspend fun insertMeal(meal: MealEntity): Result<Unit> = runCatching {
        mealDao.insertMeal(meal)
    }.onFailure { e ->
        Log.e(TAG, "Error inserting meal: ${meal.name}", e)
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
}
