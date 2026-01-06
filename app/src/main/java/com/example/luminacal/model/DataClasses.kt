package com.example.luminacal.model

enum class MealType {
    BREAKFAST, LUNCH, DINNER, SNACK
}

data class Macros(
    val protein: Int,
    val carbs: Int,
    val fat: Int
)

data class CalorieState(
    val consumed: Int,
    val target: Int
)

data class HistoryEntry(
    val id: Long,
    val name: String,
    val timestamp: Long,
    val calories: Int,
    val macros: Macros,
    val type: MealType
)

data class FoodItem(
    val id: Int,
    val title: String,
    val calories: Int,
    val time: String,
    val image: String,
    val tag: String
)

data class ScanResult(
    val name: String,
    val confidence: Float,
    val calories: Int,
    val macros: Macros,
    val ingredients: List<String>
)

/**
 * Data point for weekly calorie chart
 */
data class DailyCalories(
    val day: String,
    val calories: Float,
    val target: Float
)

/**
 * Data point for weight chart
 */
data class WeightPoint(
    val date: String,
    val weight: Float
)
