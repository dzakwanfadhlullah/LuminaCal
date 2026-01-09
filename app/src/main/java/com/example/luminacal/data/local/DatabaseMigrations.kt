package com.example.luminacal.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for LuminaCal
 * 
 * This file contains all migration objects needed to safely upgrade
 * the database schema without losing user data.
 * 
 * Current schema (v7):
 * - meal_entries: id, name, time, calories, protein, carbs, fat, type, date
 * - health_metrics: id, userName, weight, targetWeight, height, age, gender, activityLevel, fitnessGoal, waterTargetMl, lastUpdated
 * - water_entries: id, amountMl, timestamp, date
 * - weight_entries: id, weightKg, date, note
 * - custom_foods: id, name, calories, protein, carbs, fat, servingSize, isFavorite, lastUsed, useCount
 */
object DatabaseMigrations {

    /**
     * Migration from version 7 to 8
     * This is a placeholder migration that does nothing but allows
     * the database to upgrade safely without losing data.
     * 
     * When you need to add schema changes, replace this with actual SQL.
     */
    val MIGRATION_7_8 = object : Migration(7, 8) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // No schema changes in v8 - this is an empty migration
            // to establish proper migration infrastructure.
            // 
            // Future changes example:
            // db.execSQL("ALTER TABLE meal_entries ADD COLUMN imageUrl TEXT DEFAULT NULL")
        }
    }

    /**
     * Get all migrations for the database builder.
     * Add new migrations to this list as they are created.
     */
    val ALL_MIGRATIONS: Array<Migration> = arrayOf(
        MIGRATION_7_8
        // Add future migrations here:
        // MIGRATION_8_9,
        // MIGRATION_9_10,
    )

    // ============================================================================
    // HISTORICAL MIGRATIONS (for reference and future fallback migrations)
    // ============================================================================
    
    /**
     * Migration 1 -> 2: Added protein, carbs, fat columns to meal_entries
     * (Reconstructed from schema analysis)
     */
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE meal_entries ADD COLUMN protein INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE meal_entries ADD COLUMN carbs INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE meal_entries ADD COLUMN fat INTEGER NOT NULL DEFAULT 0")
        }
    }

    /**
     * Migration 2 -> 3: Added MealType enum column
     */
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE meal_entries ADD COLUMN type TEXT NOT NULL DEFAULT 'LUNCH'")
        }
    }

    /**
     * Migration 3 -> 4: Added water_entries table
     */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS water_entries (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    amountMl INTEGER NOT NULL,
                    timestamp INTEGER NOT NULL,
                    date TEXT NOT NULL
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS index_water_entries_date ON water_entries(date)")
        }
    }

    /**
     * Migration 4 -> 5: Added weight_entries table
     */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS weight_entries (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    weightKg REAL NOT NULL,
                    date INTEGER NOT NULL,
                    note TEXT
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS index_weight_entries_date ON weight_entries(date)")
        }
    }

    /**
     * Migration 5 -> 6: Added custom_foods table
     */
    val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS custom_foods (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    calories INTEGER NOT NULL,
                    protein INTEGER NOT NULL,
                    carbs INTEGER NOT NULL,
                    fat INTEGER NOT NULL,
                    servingSize TEXT NOT NULL DEFAULT '1 serving',
                    isFavorite INTEGER NOT NULL DEFAULT 0,
                    lastUsed INTEGER NOT NULL,
                    useCount INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent())
            db.execSQL("CREATE INDEX IF NOT EXISTS index_custom_foods_name ON custom_foods(name)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_custom_foods_lastUsed ON custom_foods(lastUsed)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_custom_foods_isFavorite ON custom_foods(isFavorite)")
        }
    }

    /**
     * Migration 6 -> 7: Added targetWeight and userName to health_metrics
     */
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE health_metrics ADD COLUMN targetWeight REAL NOT NULL DEFAULT 65.0")
            db.execSQL("ALTER TABLE health_metrics ADD COLUMN userName TEXT NOT NULL DEFAULT 'User'")
        }
    }

    /**
     * Complete migration chain from v1 to current version.
     * Use this if you need to support upgrades from very old versions.
     */
    val COMPLETE_MIGRATION_CHAIN: Array<Migration> = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3,
        MIGRATION_3_4,
        MIGRATION_4_5,
        MIGRATION_5_6,
        MIGRATION_6_7,
        MIGRATION_7_8
    )
}
