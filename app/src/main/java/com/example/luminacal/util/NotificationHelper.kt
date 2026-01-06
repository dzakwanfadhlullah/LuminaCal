package com.example.luminacal.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.luminacal.MainActivity
import com.example.luminacal.R
import com.example.luminacal.model.MotivationalQuotes
import com.example.luminacal.model.ReminderAction

object NotificationHelper {
    private const val CHANNEL_REMINDERS_ID = "meal_reminders"
    private const val CHANNEL_SUMMARY_ID = "daily_summary"
    private const val CHANNEL_FORGOT_ID = "forgot_to_log"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS_ID,
                "Meal Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to log your meals"
            }

            val summaryChannel = NotificationChannel(
                CHANNEL_SUMMARY_ID,
                "Daily Summary",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "End of day progress summary"
            }
            
            val forgotChannel = NotificationChannel(
                CHANNEL_FORGOT_ID,
                "Forgot to Log",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminder if you haven't logged meals today"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(reminderChannel)
            manager.createNotificationChannel(summaryChannel)
            manager.createNotificationChannel(forgotChannel)
        }
    }

    fun showMealReminder(context: Context, mealType: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Get motivational quote for this meal
        val quote = MotivationalQuotes.getQuoteForMeal(mealType)
        
        // Snooze action intents
        val snooze15Intent = createActionIntent(context, ReminderAction.SNOOZE_15, mealType)
        val snooze30Intent = createActionIntent(context, ReminderAction.SNOOZE_30, mealType)

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time for $mealType! 🍽️")
            .setContentText("$quote")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$quote\n\nTap to log your $mealType now."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_recent_history, "15 min", snooze15Intent)
            .addAction(android.R.drawable.ic_menu_recent_history, "30 min", snooze30Intent)

        try {
            NotificationManagerCompat.from(context).notify(mealType.hashCode(), builder.build())
        } catch (e: SecurityException) {
            // Permission not granted, silently fail
        }
    }
    
    fun showForgotToLogReminder(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 2, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val quote = MotivationalQuotes.getRandomForgotQuote()

        val builder = NotificationCompat.Builder(context, CHANNEL_FORGOT_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Did you eat today? 🤔")
            .setContentText(quote)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$quote\n\nWe noticed you haven't logged any meals today. Tap to add your meals and keep your streak going!"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(998, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun showDailySummary(context: Context, consumed: Int, target: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val percent = if (target > 0) (consumed.toFloat() / target * 100).toInt() else 0
        val emoji = when {
            percent >= 100 -> "🎉"
            percent >= 80 -> "💪"
            percent >= 50 -> "👍"
            else -> "📊"
        }
        val message = "Today: $consumed / $target kcal ($percent%) $emoji"
        val encouragement = when {
            percent >= 100 -> "Great job hitting your goal! 🏆"
            percent >= 80 -> "So close to your goal! Keep it up!"
            percent >= 50 -> "Good progress today!"
            else -> "Every day is a fresh start! 🌟"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARY_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Progress Summary 📈")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\n\n$encouragement"))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(999, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
    
    private fun createActionIntent(context: Context, action: ReminderAction, mealType: String): PendingIntent {
        val intent = Intent(action.action).apply {
            setPackage(context.packageName)
            putExtra("meal_type", mealType)
        }
        return PendingIntent.getBroadcast(
            context,
            action.ordinal,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    
    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
    
    /**
     * Enhanced daily summary with macro breakdown
     */
    fun showDailySummaryWithMacros(
        context: Context,
        consumed: Int,
        target: Int,
        protein: Int,
        carbs: Int,
        fat: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val percent = if (target > 0) (consumed.toFloat() / target * 100).toInt() else 0
        val emoji = when {
            percent >= 100 -> "🎉"
            percent >= 80 -> "💪"
            percent >= 50 -> "👍"
            else -> "📊"
        }
        
        val macroLine = "🥩 ${protein}g  |  🍞 ${carbs}g  |  🥑 ${fat}g"
        val message = "Today: $consumed / $target kcal ($percent%) $emoji"
        val encouragement = when {
            percent >= 100 -> "Great job hitting your goal! 🏆"
            percent >= 80 -> "So close to your goal! Keep it up!"
            percent >= 50 -> "Good progress today!"
            else -> "Every day is a fresh start! 🌟"
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARY_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Daily Progress Summary 📈")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\n\nMacros: $macroLine\n\n$encouragement"))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(999, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
    
    /**
     * Weekly progress report (Sunday evening)
     */
    fun showWeeklyReport(
        context: Context,
        avgCalories: Int,
        targetCalories: Int,
        totalDaysLogged: Int,
        weightChange: Float?,
        loggingStreak: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 3, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val avgPercent = if (targetCalories > 0) (avgCalories.toFloat() / targetCalories * 100).toInt() else 0
        val weightChangeText = when {
            weightChange == null -> ""
            weightChange < 0 -> "📉 Lost ${String.format("%.1f", kotlin.math.abs(weightChange))}kg this week!"
            weightChange > 0 -> "📈 Gained ${String.format("%.1f", weightChange)}kg this week"
            else -> "⚖️ Weight stable this week"
        }
        
        val streakEmoji = when {
            loggingStreak >= 30 -> "🔥🔥🔥"
            loggingStreak >= 14 -> "🔥🔥"
            loggingStreak >= 7 -> "🔥"
            else -> "📊"
        }
        
        val highlights = buildString {
            append("📅 This Week's Highlights:\n\n")
            append("• Avg calories: $avgCalories kcal ($avgPercent% of goal)\n")
            append("• Days logged: $totalDaysLogged / 7\n")
            append("• Logging streak: $loggingStreak days $streakEmoji\n")
            if (weightChangeText.isNotEmpty()) {
                append("• $weightChangeText")
            }
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARY_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Weekly Progress Report 📊")
            .setContentText("Your week in review - tap to see details!")
            .setStyle(NotificationCompat.BigTextStyle().bigText(highlights))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(997, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
    
    /**
     * Goal achievement celebration
     */
    fun showGoalAchievement(context: Context, consumed: Int, target: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 4, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARY_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎉 Goal Achieved! 🎉")
            .setContentText("You hit your $target kcal target today!")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "Amazing work! 🏆\n\nYou've consumed $consumed kcal and hit your daily goal of $target kcal.\n\nKeep up the consistency! 💪"
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(996, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
    
    /**
     * Streak achievement notification
     */
    fun showStreakAchievement(context: Context, streakDays: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 5, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val (emoji, title, message) = when (streakDays) {
            7 -> Triple("🔥", "One Week Streak!", "You've logged meals for 7 days straight!")
            14 -> Triple("🔥🔥", "Two Week Streak!", "14 days of consistent logging!")
            30 -> Triple("🏆", "One Month Streak!", "30 days! You're a logging legend!")
            60 -> Triple("👑", "Two Month Streak!", "60 days of dedication!")
            100 -> Triple("💎", "100 Day Streak!", "Incredible consistency!")
            else -> Triple("🔥", "$streakDays Day Streak!", "Keep the momentum going!")
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARY_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$emoji $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\n\nYou're building an amazing habit! Consistency is the key to success. 💪"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(995, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
    
    /**
     * Weight milestone notification
     */
    fun showWeightMilestone(context: Context, milestoneKg: Int, direction: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 6, intent, 
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val (emoji, message) = when {
            direction == "lost" && milestoneKg >= 10 -> Pair("🏆", "You've lost $milestoneKg kg! Incredible achievement!")
            direction == "lost" && milestoneKg >= 5 -> Pair("🎉", "You've lost $milestoneKg kg! Great progress!")
            direction == "lost" -> Pair("💪", "You've lost $milestoneKg kg! Keep going!")
            else -> Pair("⚖️", "Weight milestone reached: $milestoneKg kg")
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARY_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("$emoji Weight Milestone!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$message\n\nYour hard work is paying off! Stay consistent with your nutrition. 🌟"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            NotificationManagerCompat.from(context).notify(994, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
