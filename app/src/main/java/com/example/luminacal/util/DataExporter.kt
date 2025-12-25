package com.example.luminacal.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.luminacal.data.local.MealEntity
import com.example.luminacal.model.HealthMetrics
import com.example.luminacal.data.repository.WeightEntry
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DataExporter(private val context: Context) {

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val fileDateFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    /**
     * Export meals to CSV format
     */
    fun exportMealsToCSV(meals: List<MealEntity>): File {
        val fileName = "luminacal_meals_${fileDateFormatter.format(Date())}.csv"
        val file = File(context.cacheDir, fileName)

        val csvContent = buildString {
            // Header
            appendLine("Date,Name,Calories,Protein (g),Carbs (g),Fat (g),Type")
            
            // Data rows
            meals.forEach { meal ->
                val date = dateFormatter.format(Date(meal.date))
                appendLine("\"$date\",\"${meal.name}\",${meal.calories},${meal.protein},${meal.carbs},${meal.fat},${meal.type.name}")
            }
        }

        file.writeText(csvContent)
        return file
    }

    /**
     * Export all data to JSON format
     */
    fun exportToJSON(
        meals: List<MealEntity>,
        health: HealthMetrics,
        weights: List<WeightEntry>
    ): File {
        val fileName = "luminacal_backup_${fileDateFormatter.format(Date())}.json"
        val file = File(context.cacheDir, fileName)

        val json = JSONObject().apply {
            put("exportDate", dateFormatter.format(Date()))
            put("appVersion", "1.0.0")

            // Health Metrics
            put("healthMetrics", JSONObject().apply {
                put("weight", health.weight)
                put("height", health.height)
                put("age", health.age)
                put("gender", health.gender.name)
                put("activityLevel", health.activityLevel.name)
                put("fitnessGoal", health.fitnessGoal.name)
                put("targetCalories", health.targetCalories)
            })

            // Meals
            put("meals", JSONArray().apply {
                meals.forEach { meal ->
                    put(JSONObject().apply {
                        put("id", meal.id)
                        put("name", meal.name)
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
        return file
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
