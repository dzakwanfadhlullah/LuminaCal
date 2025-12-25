package com.example.luminacal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MealEntity::class, HealthMetricsEntity::class, WaterEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LuminaDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun healthMetricsDao(): HealthMetricsDao
    abstract fun waterDao(): WaterDao

    companion object {
        @Volatile
        private var INSTANCE: LuminaDatabase? = null

        fun getDatabase(context: Context): LuminaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LuminaDatabase::class.java,
                    "lumina_database"
                )
                    .fallbackToDestructiveMigration() // Dev phase - destroy and recreate
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
