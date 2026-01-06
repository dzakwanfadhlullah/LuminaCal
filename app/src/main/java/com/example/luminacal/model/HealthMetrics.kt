package com.example.luminacal.model

import com.example.luminacal.R
import kotlin.math.abs

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class ActivityLevel(val multiplier: Float, val labelResId: Int) {
    SEDENTARY(1.2f, R.string.activity_sedentary),
    LIGHT(1.375f, R.string.activity_light),
    MODERATE(1.55f, R.string.activity_moderate),
    ACTIVE(1.725f, R.string.activity_active),
    EXTRA_ACTIVE(1.9f, R.string.activity_extra)
}

enum class FitnessGoal(val calorieAdjustment: Int, val labelResId: Int) {
    LOSE_WEIGHT(-500, R.string.goal_lose_weight),
    MAINTAIN(0, R.string.goal_maintain),
    GAIN_MUSCLE(300, R.string.goal_gain_muscle)
}

/**
 * BMI Category based on WHO classification
 */
enum class BmiCategory(val label: String, val colorHex: Long) {
    UNDERWEIGHT("Underweight", 0xFF3B82F6),    // Blue
    NORMAL("Normal", 0xFF22C55E),               // Green
    OVERWEIGHT("Overweight", 0xFFF59E0B),       // Amber
    OBESE("Obese", 0xFFEF4444)                  // Red
}

data class HealthMetrics(
    val userName: String = "User",
    val weight: Float = 70f,       // kg - current weight
    val targetWeight: Float = 65f, // kg - goal weight
    val height: Float = 170f,      // cm
    val age: Int = 25,
    val gender: Gender = Gender.MALE,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val fitnessGoal: FitnessGoal = FitnessGoal.MAINTAIN,
    val waterTargetMl: Int = 2000
) {
    // Avatar seed based on user name for consistent avatar
    val avatarSeed: String
        get() = userName.replace(" ", "").take(10)
    
    // BMI Calculation: weight(kg) / height(m)²
    val bmi: Float
        get() {
            val heightInMeters = height / 100f
            return if (heightInMeters > 0) weight / (heightInMeters * heightInMeters) else 0f
        }
    
    // BMI Category based on WHO classification
    val bmiCategory: BmiCategory
        get() = when {
            bmi < 18.5f -> BmiCategory.UNDERWEIGHT
            bmi < 25f -> BmiCategory.NORMAL
            bmi < 30f -> BmiCategory.OVERWEIGHT
            else -> BmiCategory.OBESE
        }
    
    // Estimated weeks to reach target weight
    // Based on: 1kg = 7700 calories, deficit/surplus from fitness goal
    val estimatedWeeksToGoal: Int?
        get() {
            val weightDifference = abs(targetWeight - weight)
            if (weightDifference < 0.5f) return 0 // Already at goal
            
            val weeklyCalorieChange = abs(fitnessGoal.calorieAdjustment) * 7
            if (weeklyCalorieChange == 0) return null // Maintaining, no timeline
            
            // 1kg = ~7700 calories
            val caloriesPerKg = 7700
            val weeksPerKg = caloriesPerKg.toFloat() / weeklyCalorieChange
            return (weightDifference * weeksPerKg).toInt().coerceAtLeast(1)
        }
    
    // BMR using Mifflin-St Jeor equation
    val bmr: Int
        get() = when (gender) {
            Gender.MALE -> ((10 * weight) + (6.25 * height) - (5 * age) + 5).toInt()
            Gender.FEMALE -> ((10 * weight) + (6.25 * height) - (5 * age) - 161).toInt()
            Gender.OTHER -> ((10 * weight) + (6.25 * height) - (5 * age) - 78).toInt() // Average
        }
    
    // TDEE = BMR × Activity Multiplier
    val tdee: Int
        get() = (bmr * activityLevel.multiplier).toInt()
    
    // Target calories based on fitness goal
    val targetCalories: Int
        get() = (tdee + fitnessGoal.calorieAdjustment).coerceAtLeast(1200)
    
    // Recommended macro split
    val recommendedProtein: Int  // grams
        get() = ((targetCalories * 0.30) / 4).toInt()  // 30% of calories, 4 cal/g
    
    val recommendedCarbs: Int    // grams
        get() = ((targetCalories * 0.40) / 4).toInt()  // 40% of calories, 4 cal/g
    
    val recommendedFat: Int      // grams
        get() = ((targetCalories * 0.30) / 9).toInt()  // 30% of calories, 9 cal/g
}
