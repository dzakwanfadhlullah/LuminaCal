package com.example.luminacal.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.luminacal.data.local.MealEntity
import com.example.luminacal.model.HealthMetrics
import com.example.luminacal.model.ExportSettings
import com.example.luminacal.model.ExportFormat
import com.example.luminacal.data.repository.WeightEntry
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DataExporter(private val context: Context) {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val fileDateFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    
    companion object {
        private const val BACKUP_FOLDER = "LuminaCal_Backups"
    }

    /**
     * Export meals to CSV format with date range filtering
     */
    fun exportMealsToCSV(
        meals: List<MealEntity>,
        startDate: Long = 0L,
        endDate: Long = System.currentTimeMillis()
    ): File {
        val filteredMeals = meals.filter { it.date in startDate..endDate }
        val fileName = "luminacal_meals_${fileDateFormatter.format(Date())}.csv"
        val file = File(context.cacheDir, fileName)

        val csvContent = buildString {
            // Header
            appendLine("Date,Name,Calories,Protein (g),Carbs (g),Fat (g),Type")
            
            // Data rows
            filteredMeals.forEach { meal ->
                val date = dateFormatter.format(Date(meal.date))
                appendLine("\"$date\",\"${meal.name}\",${meal.calories},${meal.protein},${meal.carbs},${meal.fat},${meal.type.name}")
            }
        }

        file.writeText(csvContent)
        return file
    }

    /**
     * Export all data to JSON format with date range filtering
     */
    fun exportToJSON(
        meals: List<MealEntity>,
        health: HealthMetrics,
        weights: List<WeightEntry>,
        startDate: Long = 0L,
        endDate: Long = System.currentTimeMillis()
    ): File {
        val filteredMeals = meals.filter { it.date in startDate..endDate }
        val filteredWeights = weights.filter { it.date in startDate..endDate }
        
        val fileName = "luminacal_backup_${fileDateFormatter.format(Date())}.json"
        val file = File(context.cacheDir, fileName)

        val json = JSONObject().apply {
            put("exportDate", dateFormatter.format(Date()))
            put("exportTimestamp", System.currentTimeMillis())
            put("appVersion", "1.0.0")
            put("dateRangeStart", startDate)
            put("dateRangeEnd", endDate)

            // Health Metrics
            put("healthMetrics", JSONObject().apply {
                put("userName", health.userName)
                put("weight", health.weight)
                put("height", health.height)
                put("age", health.age)
                put("gender", health.gender.name)
                put("activityLevel", health.activityLevel.name)
                put("fitnessGoal", health.fitnessGoal.name)
                put("targetCalories", health.targetCalories)
                put("targetWeight", health.targetWeight ?: JSONObject.NULL)
                put("waterTargetMl", health.waterTargetMl)
            })

            // Meals
            put("meals", JSONArray().apply {
                filteredMeals.forEach { meal ->
                    put(JSONObject().apply {
                        put("id", meal.id)
                        put("name", meal.name)
                        put("time", meal.time)
                        put("date", meal.date)
                        put("calories", meal.calories)
                        put("protein", meal.protein)
                        put("carbs", meal.carbs)
                        put("fat", meal.fat)
                        put("type", meal.type.name)
                    })
                }
            })

            // Weight History
            put("weightHistory", JSONArray().apply {
                filteredWeights.forEach { entry ->
                    put(JSONObject().apply {
                        put("id", entry.id)
                        put("weightKg", entry.weightKg)
                        put("date", entry.date)
                        put("note", entry.note ?: JSONObject.NULL)
                    })
                }
            })
        }

        file.writeText(json.toString(2))
        return file
    }
    
    /**
     * Create automatic backup to persistent storage
     */
    fun createAutoBackup(
        meals: List<MealEntity>,
        health: HealthMetrics,
        weights: List<WeightEntry>
    ): File? {
        return try {
            val backupDir = getBackupDirectory()
            val fileName = "luminacal_autobackup_${fileDateFormatter.format(Date())}.json"
            val file = File(backupDir, fileName)
            
            val json = JSONObject().apply {
                put("exportDate", dateFormatter.format(Date()))
                put("exportTimestamp", System.currentTimeMillis())
                put("appVersion", "1.0.0")
                put("isAutoBackup", true)

                put("healthMetrics", JSONObject().apply {
                    put("userName", health.userName)
                    put("weight", health.weight)
                    put("height", health.height)
                    put("age", health.age)
                    put("gender", health.gender.name)
                    put("activityLevel", health.activityLevel.name)
                    put("fitnessGoal", health.fitnessGoal.name)
                    put("targetCalories", health.targetCalories)
                    put("targetWeight", health.targetWeight ?: JSONObject.NULL)
                    put("waterTargetMl", health.waterTargetMl)
                })

                put("meals", JSONArray().apply {
                    meals.forEach { meal ->
                        put(JSONObject().apply {
                            put("id", meal.id)
                            put("name", meal.name)
                            put("time", meal.time)
                            put("date", meal.date)
                            put("calories", meal.calories)
                            put("protein", meal.protein)
                            put("carbs", meal.carbs)
                            put("fat", meal.fat)
                            put("type", meal.type.name)
                        })
                    }
                })

                put("weightHistory", JSONArray().apply {
                    weights.forEach { entry ->
                        put(JSONObject().apply {
                            put("id", entry.id)
                            put("weightKg", entry.weightKg)
                            put("date", entry.date)
                            put("note", entry.note ?: JSONObject.NULL)
                        })
                    }
                })
            }

            file.writeText(json.toString(2))
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get backup directory, creating if needed
     */
    private fun getBackupDirectory(): File {
        val backupDir = File(context.getExternalFilesDir(null), BACKUP_FOLDER)
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        return backupDir
    }
    
    /**
     * Clean up old backups, keeping only the most recent ones
     */
    fun cleanupOldBackups(maxToKeep: Int = 4) {
        val backupDir = getBackupDirectory()
        val backupFiles = backupDir.listFiles { file ->
            file.extension == "json" && file.name.startsWith("luminacal")
        }?.sortedByDescending { it.lastModified() } ?: return
        
        if (backupFiles.size > maxToKeep) {
            backupFiles.drop(maxToKeep).forEach { it.delete() }
        }
    }
    
    /**
     * Get list of available backup files
     */
    fun getAvailableBackups(): List<BackupFileInfo> {
        val backupDir = getBackupDirectory()
        return backupDir.listFiles { file ->
            file.extension == "json" && file.name.startsWith("luminacal")
        }?.map { file ->
            BackupFileInfo(
                filename = file.name,
                path = file.absolutePath,
                sizeBytes = file.length(),
                createdAt = file.lastModified()
            )
        }?.sortedByDescending { it.createdAt } ?: emptyList()
    }

    /**
     * Share file using Android share sheet
     */
    fun shareFile(file: File, mimeType: String = "text/*") {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(intent, "Export Data").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

/**
 * Backup file information
 */
data class BackupFileInfo(
    val filename: String,
    val path: String,
    val sizeBytes: Long,
    val createdAt: Long
) {
    val formattedSize: String
        get() = when {
            sizeBytes < 1024 -> "$sizeBytes B"
            sizeBytes < 1024 * 1024 -> "${sizeBytes / 1024} KB"
            else -> "${sizeBytes / (1024 * 1024)} MB"
        }
    
    val formattedDate: String
        get() = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(createdAt))
}

