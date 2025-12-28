package com.example.luminacal.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.luminacal.data.local.LuminaDatabase
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

class DailySummaryWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = LuminaDatabase.getDatabase(applicationContext)
        val metrics = database.healthMetricsDao().getHealthMetrics().firstOrNull()?.toHealthMetrics() ?: return Result.success()
        
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startOfDay = calendar.timeInMillis
        
        val todayMeals = database.mealDao().getAllMeals().firstOrNull()?.filter { it.date >= startOfDay } ?: emptyList()
        val totalConsumed = todayMeals.sumOf { it.calories }

        NotificationHelper.showDailySummary(
            applicationContext,
            totalConsumed,
            metrics.targetCalories
        )
        
        return Result.success()
    }
}
