package com.example.luminacal.util

import android.content.Context
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {

    fun scheduleMealReminders(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // Breakfast: 8:00 AM
        scheduleReminder(workManager, "Breakfast", 8, 0)
        // Lunch: 12:00 PM
        scheduleReminder(workManager, "Lunch", 12, 0)
        // Dinner: 7:00 PM
        scheduleReminder(workManager, "Dinner", 19, 0)
        
        // Daily Summary: 9:00 PM
        scheduleDailySummary(workManager, 21, 0)
    }

    private fun scheduleReminder(workManager: WorkManager, mealType: String, hour: Int, minute: Int) {
        val delay = calculateDelay(hour, minute)
        
        val workRequest = PeriodicWorkRequestBuilder<MealReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf("meal_type" to mealType))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build())
            .build()

        workManager.enqueueUniquePeriodicWork(
            "reminder_$mealType",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun scheduleDailySummary(workManager: WorkManager, hour: Int, minute: Int) {
        val delay = calculateDelay(hour, minute)
        
        val workRequest = PeriodicWorkRequestBuilder<DailySummaryWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build())
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_summary",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    private fun calculateDelay(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }

    fun cancelAllReminders(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }
}
