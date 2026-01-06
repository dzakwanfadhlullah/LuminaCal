package com.example.luminacal.util

import android.content.Context
import android.net.Uri
import com.example.luminacal.data.local.MealEntity
import com.example.luminacal.model.HealthMetrics
import com.example.luminacal.model.MealType
import com.example.luminacal.model.Gender
import com.example.luminacal.model.ActivityLevel
import com.example.luminacal.model.FitnessGoal
import com.example.luminacal.data.repository.WeightEntry
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * Merge strategy options for data import
 */
enum class MergeStrategy(val label: String, val description: String) {
    SKIP_DUPLICATES("Skip Duplicates", "Keep existing data, only add new entries"),
    OVERWRITE_EXISTING("Overwrite Existing", "Replace existing data with backup data"),
    MERGE_COMBINE("Merge (Combine)", "Combine both, prefer backup for conflicts")
}

/**
 * Import preview data
 */
data class ImportPreview(
    val backupDate: String,
    val appVersion: String,
    val mealsCount: Int,
    val weightEntriesCount: Int,
    val hasHealthMetrics: Boolean,
    val dateRangeStart: Long?,
    val dateRangeEnd: Long?,
    val isValid: Boolean,
    val errorMessage: String? = null
) {
    val dateRangeFormatted: String
        get() {
            if (dateRangeStart == null || dateRangeEnd == null) return "All time"
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            return "${sdf.format(Date(dateRangeStart))} - ${sdf.format(Date(dateRangeEnd))}"
        }
}

/**
 * Import result
 */
data class ImportResult(
    val success: Boolean,
    val mealsImported: Int = 0,
    val mealsSkipped: Int = 0,
    val weightEntriesImported: Int = 0,
    val weightEntriesSkipped: Int = 0,
    val healthMetricsImported: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Clear data confirmation state
 */
data class ClearDataState(
    val showExportReminder: Boolean = true,
    val confirmationText: String = "",
    val gracePeriodSeconds: Int = 5,
    val isCountingDown: Boolean = false
) {
    val isConfirmed: Boolean
        get() = confirmationText.uppercase() == "DELETE"
}

/**
 * Utility class for importing data from backup files
 */
class DataImporter(private val context: Context) {
    
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    /**
     * Preview backup file without importing
     */
    fun previewBackup(uri: Uri): ImportPreview {
        return try {
            val jsonString = readFileContent(uri)
            val json = JSONObject(jsonString)
            
            val mealsArray = json.optJSONArray("meals")
            val weightsArray = json.optJSONArray("weightHistory")
            val healthMetrics = json.optJSONObject("healthMetrics")
            
            ImportPreview(
                backupDate = json.optString("exportDate", "Unknown"),
                appVersion = json.optString("appVersion", "Unknown"),
                mealsCount = mealsArray?.length() ?: 0,
                weightEntriesCount = weightsArray?.length() ?: 0,
                hasHealthMetrics = healthMetrics != null,
                dateRangeStart = json.optLong("dateRangeStart", 0).takeIf { it > 0 },
                dateRangeEnd = json.optLong("dateRangeEnd", 0).takeIf { it > 0 },
                isValid = true
            )
        } catch (e: Exception) {
            ImportPreview(
                backupDate = "",
                appVersion = "",
                mealsCount = 0,
                weightEntriesCount = 0,
                hasHealthMetrics = false,
                dateRangeStart = null,
                dateRangeEnd = null,
                isValid = false,
                errorMessage = "Invalid backup file: ${e.message}"
            )
        }
    }
    
    /**
     * Parse meals from backup file
     */
    fun parseMeals(uri: Uri): List<MealEntity> {
        return try {
            val jsonString = readFileContent(uri)
            val json = JSONObject(jsonString)
            val mealsArray = json.optJSONArray("meals") ?: return emptyList()
            
            (0 until mealsArray.length()).map { index ->
                val mealJson = mealsArray.getJSONObject(index)
                MealEntity(
                    id = 0, // Will be auto-generated on insert
                    name = mealJson.getString("name"),
                    time = mealJson.optString("time", "08:00"),
                    calories = mealJson.getInt("calories"),
                    protein = mealJson.getInt("protein"),
                    carbs = mealJson.getInt("carbs"),
                    fat = mealJson.getInt("fat"),
                    type = try {
                        MealType.valueOf(mealJson.getString("type"))
                    } catch (e: Exception) {
                        MealType.SNACK
                    },
                    date = mealJson.getLong("date")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Parse weight entries from backup file
     */
    fun parseWeightEntries(uri: Uri): List<WeightEntry> {
        return try {
            val jsonString = readFileContent(uri)
            val json = JSONObject(jsonString)
            val weightsArray = json.optJSONArray("weightHistory") ?: return emptyList()
            
            (0 until weightsArray.length()).map { index ->
                val weightJson = weightsArray.getJSONObject(index)
                WeightEntry(
                    id = 0, // Will be auto-generated on insert
                    weightKg = weightJson.getDouble("weightKg").toFloat(),
                    date = weightJson.getLong("date"),
                    note = weightJson.optString("note").takeIf { it.isNotEmpty() && it != "null" }
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Parse health metrics from backup file
     */
    fun parseHealthMetrics(uri: Uri): HealthMetrics? {
        return try {
            val jsonString = readFileContent(uri)
            val json = JSONObject(jsonString)
            val metricsJson = json.optJSONObject("healthMetrics") ?: return null
            
            HealthMetrics(
                userName = metricsJson.optString("userName", "User"),
                weight = metricsJson.optDouble("weight", 70.0).toFloat(),
                targetWeight = metricsJson.optDouble("targetWeight", 65.0).toFloat(),
                height = metricsJson.optDouble("height", 170.0).toFloat(),
                age = metricsJson.optInt("age", 25),
                gender = try {
                    Gender.valueOf(metricsJson.optString("gender", "MALE"))
                } catch (e: Exception) {
                    Gender.MALE
                },
                activityLevel = try {
                    ActivityLevel.valueOf(metricsJson.optString("activityLevel", "MODERATE"))
                } catch (e: Exception) {
                    ActivityLevel.MODERATE
                },
                fitnessGoal = try {
                    FitnessGoal.valueOf(metricsJson.optString("fitnessGoal", "MAINTAIN"))
                } catch (e: Exception) {
                    FitnessGoal.MAINTAIN
                },
                waterTargetMl = metricsJson.optInt("waterTargetMl", 2000)
            )
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Filter meals based on merge strategy
     */
    fun filterMealsForImport(
        backupMeals: List<MealEntity>,
        existingMeals: List<MealEntity>,
        strategy: MergeStrategy
    ): Pair<List<MealEntity>, Int> {
        return when (strategy) {
            MergeStrategy.SKIP_DUPLICATES -> {
                val existingDates = existingMeals.map { it.date to it.name }.toSet()
                val toImport = backupMeals.filter { meal ->
                    (meal.date to meal.name) !in existingDates
                }
                toImport to (backupMeals.size - toImport.size)
            }
            MergeStrategy.OVERWRITE_EXISTING -> {
                backupMeals to 0
            }
            MergeStrategy.MERGE_COMBINE -> {
                val existingDates = existingMeals.map { it.date to it.name }.toSet()
                val newMeals = backupMeals.filter { meal ->
                    (meal.date to meal.name) !in existingDates
                }
                newMeals to (backupMeals.size - newMeals.size)
            }
        }
    }
    
    /**
     * Filter weight entries based on merge strategy
     */
    fun filterWeightsForImport(
        backupWeights: List<WeightEntry>,
        existingWeights: List<WeightEntry>,
        strategy: MergeStrategy
    ): Pair<List<WeightEntry>, Int> {
        return when (strategy) {
            MergeStrategy.SKIP_DUPLICATES -> {
                val existingDates = existingWeights.map { it.date }.toSet()
                val toImport = backupWeights.filter { it.date !in existingDates }
                toImport to (backupWeights.size - toImport.size)
            }
            MergeStrategy.OVERWRITE_EXISTING -> {
                backupWeights to 0
            }
            MergeStrategy.MERGE_COMBINE -> {
                val existingDates = existingWeights.map { it.date }.toSet()
                val newWeights = backupWeights.filter { it.date !in existingDates }
                newWeights to (backupWeights.size - newWeights.size)
            }
        }
    }
    
    /**
     * Read file content from URI
     */
    private fun readFileContent(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw Exception("Cannot open file")
        return BufferedReader(InputStreamReader(inputStream)).use { it.readText() }
    }
}

/**
 * Data deletion manager with safety features
 */
object DataDeletionManager {
    
    private const val REQUIRED_CONFIRMATION = "DELETE"
    private const val GRACE_PERIOD_SECONDS = 5
    
    /**
     * Validate deletion confirmation text
     */
    fun isConfirmationValid(text: String): Boolean {
        return text.uppercase().trim() == REQUIRED_CONFIRMATION
    }
    
    /**
     * Get grace period in seconds
     */
    fun getGracePeriodSeconds(): Int = GRACE_PERIOD_SECONDS
    
    /**
     * Messages for clear data flow
     */
    object Messages {
        const val EXPORT_REMINDER = "⚠️ Have you exported your data? This action cannot be undone!"
        const val CONFIRMATION_PROMPT = "Type \"DELETE\" to confirm"
        const val COUNTDOWN_MESSAGE = "Deleting in %d seconds... Tap Cancel to abort"
        const val SUCCESS_MESSAGE = "All data has been cleared"
    }
}
