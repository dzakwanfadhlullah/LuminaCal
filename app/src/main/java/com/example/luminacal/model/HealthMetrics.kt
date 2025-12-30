package com.example.luminacal.model

import com.example.luminacal.R

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

data class HealthMetrics(
    val userName: String = "User",
    val weight: Float = 70f,       // kg
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
