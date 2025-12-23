package com.example.luminacal.data.repository

import com.example.luminacal.data.local.MealDao
import com.example.luminacal.data.local.MealEntity
import kotlinx.coroutines.flow.Flow

class MealRepository(private val mealDao: MealDao) {
    val allMeals: Flow<List<MealEntity>> = mealDao.getAllMeals()

    fun getMealsForDay(startOfDay: Long, endOfDay: Long): Flow<List<MealEntity>> {
        return mealDao.getMealsForDay(startOfDay, endOfDay)
    }

    suspend fun insertMeal(meal: MealEntity) {
        mealDao.insertMeal(meal)
    }

    suspend fun deleteMeal(meal: MealEntity) {
        mealDao.deleteMeal(meal)
    }
}
