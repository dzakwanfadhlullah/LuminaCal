package com.example.luminacal.model

/**
 * Reminder settings for customizable meal reminders
 */
data class ReminderSettings(
    val breakfastEnabled: Boolean = true,
    val breakfastHour: Int = 8,
    val breakfastMinute: Int = 0,
    
    val lunchEnabled: Boolean = true,
    val lunchHour: Int = 12,
    val lunchMinute: Int = 0,
    
    val dinnerEnabled: Boolean = true,
    val dinnerHour: Int = 19,
    val dinnerMinute: Int = 0,
    
    // Days of week (0 = Sunday, 6 = Saturday)
    val enabledDays: Set<Int> = setOf(0, 1, 2, 3, 4, 5, 6),
    
    // "Forgot to Log" reminder
    val forgotToLogEnabled: Boolean = true,
    val forgotToLogHour: Int = 20, // 8 PM
    val forgotToLogMinute: Int = 30,
    
    // Daily summary
    val dailySummaryEnabled: Boolean = true,
    val dailySummaryHour: Int = 21, // 9 PM
    val dailySummaryMinute: Int = 0,
    
    // Snooze settings (in minutes)
    val lastSnoozedMeal: String? = null,
    val snoozeUntil: Long? = null
) {
    companion object {
        val SNOOZE_OPTIONS = listOf(15, 30, 60) // minutes
    }
}

/**
 * Motivational quotes for meal reminders
 */
object MotivationalQuotes {
    private val quotes = listOf(
        "Small progress is still progress! 💪",
        "You're one meal closer to your goal! 🎯",
        "Consistency is key! Keep going! 🔑",
        "Every healthy choice matters! 🌱",
        "Your future self will thank you! ⭐",
        "Stay on track, you've got this! 🚀",
        "Healthy eating is self-love! ❤️",
        "One day at a time! 📅",
        "You're stronger than your cravings! 💪",
        "Making progress every day! 📈",
        "Fuel your body right! ⛽",
        "Nutrition is your superpower! 🦸",
        "Keep building healthy habits! 🏗️",
        "Your health is your wealth! 💎",
        "Consistency beats perfection! ✨"
    )
    
    private val forgotToLogQuotes = listOf(
        "Did you forget to log something today? 🤔",
        "Your food diary misses you! 📝",
        "Quick reminder to log your meals! 🍽️",
        "Stay accountable - log your meals! 📊",
        "Don't break your streak! Log now! 🔥"
    )
    
    fun getRandomQuote(): String = quotes.random()
    
    fun getRandomForgotQuote(): String = forgotToLogQuotes.random()
    
    fun getQuoteForMeal(mealType: String): String {
        return when (mealType.lowercase()) {
            "breakfast" -> listOf(
                "Start your day right! 🌅",
                "Breakfast fuels your morning! ☀️",
                "Morning fuel for champions! 🏆"
            ).random()
            "lunch" -> listOf(
                "Midday fuel check! 🌞",
                "Power through the afternoon! 💪",
                "Keep your energy up! ⚡"
            ).random()
            "dinner" -> listOf(
                "End your day on a healthy note! 🌙",
                "Dinner time! Log it! 🍽️",
                "Almost done with today's goals! 🎯"
            ).random()
            else -> getRandomQuote()
        }
    }
}

/**
 * Reminder action for notification intents
 */
enum class ReminderAction(val action: String) {
    SNOOZE_15("com.example.luminacal.SNOOZE_15"),
    SNOOZE_30("com.example.luminacal.SNOOZE_30"),
    SNOOZE_60("com.example.luminacal.SNOOZE_60"),
    DISMISS("com.example.luminacal.DISMISS_REMINDER"),
    QUICK_LOG("com.example.luminacal.QUICK_LOG")
}

/**
 * Notification preferences for user customization
 */
data class NotificationPreferences(
    // Toggle each notification type
    val mealRemindersEnabled: Boolean = true,
    val dailySummaryEnabled: Boolean = true,
    val weeklySummaryEnabled: Boolean = true,
    val forgotToLogEnabled: Boolean = true,
    val goalAchievementEnabled: Boolean = true,
    val streakAchievementEnabled: Boolean = true,
    val weightMilestoneEnabled: Boolean = true,
    
    // Quiet hours
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: Int = 22,  // 10 PM
    val quietHoursEnd: Int = 7,     // 7 AM
    
    // Sound & vibration
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
) {
    /**
     * Check if current time is within quiet hours
     */
    fun isInQuietHours(): Boolean {
        if (!quietHoursEnabled) return false
        
        val now = java.util.Calendar.getInstance()
        val currentHour = now.get(java.util.Calendar.HOUR_OF_DAY)
        
        return if (quietHoursStart > quietHoursEnd) {
            // Quiet hours span midnight (e.g., 22:00 - 07:00)
            currentHour >= quietHoursStart || currentHour < quietHoursEnd
        } else {
            // Normal range (e.g., 01:00 - 06:00)
            currentHour in quietHoursStart until quietHoursEnd
        }
    }
}
