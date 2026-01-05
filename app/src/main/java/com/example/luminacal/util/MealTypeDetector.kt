package com.example.luminacal.util

import com.example.luminacal.model.MealType
import java.util.Calendar

/**
 * Utility object for smart meal type detection based on time of day.
 * 
 * Default time ranges:
 * - Breakfast: 05:00 - 10:59
 * - Lunch: 11:00 - 14:59
 * - Snack (Afternoon): 15:00 - 17:59
 * - Dinner: 18:00 - 21:59
 * - Snack (Late night): 22:00 - 04:59
 */
object MealTypeDetector {
    
    // Time range constants (24-hour format)
    private const val BREAKFAST_START = 5
    private const val BREAKFAST_END = 10
    private const val LUNCH_START = 11
    private const val LUNCH_END = 14
    private const val AFTERNOON_SNACK_START = 15
    private const val AFTERNOON_SNACK_END = 17
    private const val DINNER_START = 18
    private const val DINNER_END = 21
    // Late night (22:00 - 04:59) defaults to SNACK
    
    /**
     * Detect meal type based on current time
     * @return appropriate MealType for the current hour
     */
    fun detectFromCurrentTime(): MealType {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return getMealTypeForHour(hour)
    }
    
    /**
     * Get meal type for a specific hour (0-23)
     * @param hour Hour in 24-hour format
     * @return appropriate MealType
     */
    fun getMealTypeForHour(hour: Int): MealType {
        return when (hour) {
            in BREAKFAST_START..BREAKFAST_END -> MealType.BREAKFAST
            in LUNCH_START..LUNCH_END -> MealType.LUNCH
            in AFTERNOON_SNACK_START..AFTERNOON_SNACK_END -> MealType.SNACK
            in DINNER_START..DINNER_END -> MealType.DINNER
            else -> MealType.SNACK // Late night or early morning defaults to snack
        }
    }
    
    /**
     * Get display name for meal type
     */
    fun getDisplayName(mealType: MealType): String {
        return when (mealType) {
            MealType.BREAKFAST -> "Breakfast"
            MealType.LUNCH -> "Lunch"
            MealType.DINNER -> "Dinner"
            MealType.SNACK -> "Snack"
        }
    }
    
    /**
     * Get emoji for meal type
     */
    fun getEmoji(mealType: MealType): String {
        return when (mealType) {
            MealType.BREAKFAST -> "🌅"
            MealType.LUNCH -> "☀️"
            MealType.DINNER -> "🌙"
            MealType.SNACK -> "🍿"
        }
    }
    
    /**
     * Get all meal types in order
     */
    fun getAllMealTypes(): List<MealType> {
        return listOf(
            MealType.BREAKFAST,
            MealType.LUNCH,
            MealType.DINNER,
            MealType.SNACK
        )
    }
}
