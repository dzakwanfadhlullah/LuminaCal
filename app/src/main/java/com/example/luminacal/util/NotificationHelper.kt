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
}

