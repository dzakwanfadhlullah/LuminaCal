package com.example.luminacal.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MealReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val mealType = inputData.getString("meal_type") ?: "Meal"
        NotificationHelper.showMealReminder(applicationContext, mealType)
        return Result.success()
    }
}
