package com.example.luminacal.model

/**
 * Beverage types for water tracking
 */
enum class BeverageType(val label: String, val emoji: String, val hydrationFactor: Float) {
    WATER("Water", "💧", 1.0f),
    TEA("Tea", "🍵", 0.9f),
    COFFEE("Coffee", "☕", 0.8f),  // Coffee has caffeine warning
    JUICE("Juice", "🧃", 0.85f),
    OTHER("Other", "🥤", 0.9f)
}

/**
 * Preset glass sizes for quick water logging
 */
enum class GlassSize(val amountMl: Int, val label: String) {
    SMALL(150, "150ml"),
    MEDIUM(250, "250ml"),
    LARGE(500, "500ml"),
    BOTTLE(750, "750ml")
}

data class WaterEntry(
    val id: Long = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val beverageType: BeverageType = BeverageType.WATER
)

data class WaterState(
    val consumed: Int = 0,              // ml consumed today
    val target: Int = 2000,             // daily goal (2L default)
    val glassCount: Int = 0,            // number of glasses (250ml each)
    val lastGlassSize: GlassSize = GlassSize.MEDIUM,  // remember last used size
    val todayEntries: List<WaterEntry> = emptyList()  // today's log entries
) {
    // Progress as percentage
    val progress: Float
        get() = (consumed.toFloat() / target).coerceIn(0f, 1f)
    
    // Display in cups (assuming 250ml per cup)
    val cupsConsumed: Float
        get() = consumed / 250f
    
    val cupsTarget: Float
        get() = target / 250f
    
    // Check if goal is reached
    val goalReached: Boolean
        get() = consumed >= target
    
    companion object {
        /**
         * Calculate smart water goal based on body weight
         * Recommended: 30-35ml per kg of body weight
         * Adjusted for activity level
         */
        fun calculateSmartGoal(
            weightKg: Float,
            activityMultiplier: Float = 1.0f  // 1.0 for sedentary, up to 1.5 for very active
        ): Int {
            val baseGoal = (weightKg * 30).toInt()  // 30ml per kg
            val adjustedGoal = (baseGoal * activityMultiplier).toInt()
            // Round to nearest 100ml and ensure minimum of 1500ml
            return ((adjustedGoal / 100) * 100).coerceIn(1500, 5000)
        }
    }
}

/**
 * Hydration streak tracking
 */
data class HydrationStreak(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastGoalMetDate: Long? = null,
    val streakProtectionUsed: Boolean = false  // one miss allowed
) {
    val hasActiveStreak: Boolean
        get() = currentStreak > 0
    
    // Check if streak is at risk (last goal met was yesterday)
    val isAtRisk: Boolean
        get() {
            if (lastGoalMetDate == null) return false
            val daysSinceGoal = (System.currentTimeMillis() - lastGoalMetDate) / (24 * 60 * 60 * 1000)
            return daysSinceGoal == 1L && !streakProtectionUsed
        }
    
    // Get current achievement level
    val currentAchievement: HydrationAchievement?
        get() = HydrationAchievement.entries
            .sortedByDescending { it.requiredDays }
            .firstOrNull { currentStreak >= it.requiredDays }
    
    // Get next achievement to unlock
    val nextAchievement: HydrationAchievement?
        get() = HydrationAchievement.entries
            .sortedBy { it.requiredDays }
            .firstOrNull { currentStreak < it.requiredDays }
    
    // Days until next achievement
    val daysToNextAchievement: Int?
        get() = nextAchievement?.let { it.requiredDays - currentStreak }
}

/**
 * Hydration achievement badges
 */
enum class HydrationAchievement(
    val title: String,
    val emoji: String,
    val requiredDays: Int,
    val description: String
) {
    HYDRATION_STARTER("Hydration Starter", "💧", 3, "3 day streak"),
    HYDRATION_HERO("Hydration Hero", "🦸", 7, "7 day streak"),
    WATER_WARRIOR("Water Warrior", "⚔️", 14, "14 day streak"),
    WATER_CHAMPION("Water Champion", "🏆", 30, "30 day streak"),
    HYDRATION_MASTER("Hydration Master", "👑", 60, "60 day streak"),
    AQUA_LEGEND("Aqua Legend", "🌊", 100, "100 day streak")
}
