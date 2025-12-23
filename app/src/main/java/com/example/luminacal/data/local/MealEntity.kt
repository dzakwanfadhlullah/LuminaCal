package com.example.luminacal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.luminacal.model.MealType

@Entity(tableName = "meal_entries")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val time: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val type: MealType,
    val date: Long // Timestamp for filtering by day
)
