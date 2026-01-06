package com.example.luminacal.model

import java.util.Calendar

/**
 * Export format options
 */
enum class ExportFormat(val extension: String, val mimeType: String, val label: String) {
    CSV("csv", "text/csv", "CSV (Spreadsheet)"),
    JSON("json", "application/json", "JSON (Full Backup)"),
    PDF("pdf", "application/pdf", "PDF (Printable)")
}

/**
 * Date range presets for export
 */
enum class DateRangePreset(val label: String, val getDays: () -> Int) {
    LAST_WEEK("Last Week", { 7 }),
    LAST_MONTH("Last Month", { 30 }),
    LAST_3_MONTHS("Last 3 Months", { 90 }),
    LAST_YEAR("Last Year", { 365 }),
    ALL_TIME("All Time", { Int.MAX_VALUE }),
    CUSTOM("Custom Range", { 0 })
}

/**
 * Export settings configuration
 */
data class ExportSettings(
    val format: ExportFormat = ExportFormat.CSV,
    val dateRangePreset: DateRangePreset = DateRangePreset.LAST_MONTH,
    val customStartDate: Long? = null,
    val customEndDate: Long? = null,
    
    // What to include
    val includeMeals: Boolean = true,
    val includeWater: Boolean = true,
    val includeWeight: Boolean = true,
    val includeHealthMetrics: Boolean = true
) {
    /**
     * Calculate start date based on preset or custom range
     */
    fun getStartDate(): Long {
        return when (dateRangePreset) {
            DateRangePreset.CUSTOM -> customStartDate ?: 0L
            DateRangePreset.ALL_TIME -> 0L
            else -> {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -dateRangePreset.getDays())
                cal.timeInMillis
            }
        }
    }
    
    /**
     * Calculate end date
     */
    fun getEndDate(): Long {
        return when (dateRangePreset) {
            DateRangePreset.CUSTOM -> customEndDate ?: System.currentTimeMillis()
            else -> System.currentTimeMillis()
        }
    }
    
    /**
     * Generate filename for export
     */
    fun generateFilename(): String {
        val sdf = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
        val timestamp = sdf.format(java.util.Date())
        return "LuminaCal_Export_$timestamp.${format.extension}"
    }
}

/**
 * Backup configuration
 */
data class BackupSettings(
    val autoBackupEnabled: Boolean = false,
    val backupDayOfWeek: Int = Calendar.SUNDAY, // 1 = Sunday, 7 = Saturday
    val maxBackupsToKeep: Int = 4,
    val lastBackupTime: Long? = null
) {
    companion object {
        val DAY_OPTIONS = listOf(
            Calendar.SUNDAY to "Sunday",
            Calendar.MONDAY to "Monday",
            Calendar.TUESDAY to "Tuesday",
            Calendar.WEDNESDAY to "Wednesday",
            Calendar.THURSDAY to "Thursday",
            Calendar.FRIDAY to "Friday",
            Calendar.SATURDAY to "Saturday"
        )
    }
    
    /**
     * Check if backup is due today
     */
    fun isBackupDue(): Boolean {
        if (!autoBackupEnabled) return false
        
        val today = Calendar.getInstance()
        if (today.get(Calendar.DAY_OF_WEEK) != backupDayOfWeek) return false
        
        // Check if already backed up today
        lastBackupTime?.let { lastTime ->
            val lastBackupCal = Calendar.getInstance().apply { timeInMillis = lastTime }
            if (lastBackupCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                lastBackupCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                return false
            }
        }
        
        return true
    }
}

/**
 * Exported data container
 */
data class ExportedData(
    val exportDate: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0",
    val meals: List<MealExport> = emptyList(),
    val waterEntries: List<WaterExport> = emptyList(),
    val weightEntries: List<WeightExport> = emptyList(),
    val healthMetrics: HealthMetricsExport? = null
)

data class MealExport(
    val id: Long,
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val mealType: String,
    val date: Long,
    val dateFormatted: String
)

data class WaterExport(
    val id: Long,
    val amountMl: Int,
    val beverageType: String,
    val timestamp: Long,
    val dateFormatted: String
)

data class WeightExport(
    val id: Long,
    val weightKg: Float,
    val note: String?,
    val date: Long,
    val dateFormatted: String
)

data class HealthMetricsExport(
    val age: Int,
    val gender: String,
    val heightCm: Int,
    val weightKg: Float,
    val activityLevel: String,
    val fitnessGoal: String,
    val targetCalories: Int,
    val targetWeight: Float?
)
