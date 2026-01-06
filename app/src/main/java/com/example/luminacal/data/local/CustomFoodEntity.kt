package com.example.luminacal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity for user's custom food entries
 * Stored separately from the built-in FoodNutritionDatabase
 */
@Entity(tableName = "custom_foods")
data class CustomFoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val servingSize: String = "1 serving",
    val isFavorite: Boolean = false,
    val lastUsed: Long = System.currentTimeMillis(),
    val useCount: Int = 0
) {
    /**
     * Convert to NutritionInfo for unified display with database foods
     */
    fun toNutritionInfo(): com.example.luminacal.data.ml.NutritionInfo {
        return com.example.luminacal.data.ml.NutritionInfo(
            name = name,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            servingSize = servingSize,
            imageUrl = null  // Custom foods don't have images by default
        )
    }
}
