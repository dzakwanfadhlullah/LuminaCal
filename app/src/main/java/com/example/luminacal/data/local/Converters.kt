package com.example.luminacal.data.local

import androidx.room.TypeConverter
import com.example.luminacal.model.MealType

class Converters {
    @TypeConverter
    fun fromMealType(value: MealType): String {
        return value.name
    }

    @TypeConverter
    fun toMealType(value: String): MealType {
        return MealType.valueOf(value)
    }
}
