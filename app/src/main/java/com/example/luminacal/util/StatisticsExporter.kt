package com.example.luminacal.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for exporting statistics as images
 */
class StatisticsExporter(private val context: Context) {

    private val fileDateFormatter = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    /**
     * Create a shareable image from statistics summary
     */
    fun createStatsSummaryImage(
        title: String,
        avgCalories: Int,
        totalDays: Int,
        proteinG: Int,
        carbsG: Int,
        fatG: Int,
        currentWeight: Float,
        weightGoal: Float,
        streak: Int
    ): File {
        val fileName = "luminacal_stats_${fileDateFormatter.format(Date())}.txt"
        val file = File(context.cacheDir, fileName)
        
        val content = buildString {
            appendLine("════════════════════════════════")
            appendLine("     📊 $title")
            appendLine("════════════════════════════════")
            appendLine()
            appendLine("🔥 Average Daily Calories: $avgCalories kcal")
            appendLine("📅 Days Tracked: $totalDays")
            appendLine()
            appendLine("── Macros ──")
            appendLine("🥩 Protein: ${proteinG}g")
            appendLine("🍞 Carbs: ${carbsG}g")
            appendLine("🧈 Fat: ${fatG}g")
            appendLine()
            appendLine("── Weight Progress ──")
            appendLine("⚖️ Current: ${String.format("%.1f", currentWeight)} kg")
            appendLine("🎯 Goal: ${String.format("%.1f", weightGoal)} kg")
            val diff = weightGoal - currentWeight
            appendLine("📉 To go: ${String.format("%+.1f", diff)} kg")
            appendLine()
            appendLine("🔥 Logging Streak: $streak days")
            appendLine()
            appendLine("────────────────────────────────")
            appendLine("Shared from LuminaCal 📱")
            appendLine("────────────────────────────────")
        }
        
        file.writeText(content)
        return file
    }

    /**
     * Share text statistics via Android share sheet
     */
    fun shareStatsAsText(
        avgCalories: Int,
        totalDays: Int,
        proteinG: Int,
        carbsG: Int,
        fatG: Int,
        currentWeight: Float,
        weightGoal: Float,
        streak: Int
    ) {
        val shareText = buildString {
            appendLine("📊 My LuminaCal Stats")
            appendLine()
            appendLine("🔥 Avg Calories: $avgCalories kcal/day")
            appendLine("📅 Days Tracked: $totalDays")
            appendLine()
            appendLine("Macros:")
            appendLine("• Protein: ${proteinG}g")
            appendLine("• Carbs: ${carbsG}g")
            appendLine("• Fat: ${fatG}g")
            appendLine()
            appendLine("⚖️ Weight: ${String.format("%.1f", currentWeight)} → ${String.format("%.1f", weightGoal)} kg")
            appendLine("🔥 Streak: $streak days")
            appendLine()
            appendLine("#LuminaCal #HealthTracking")
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(intent, "Share Stats").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
    
    /**
     * Export statistics summary to a file and share
     */
    fun exportAndShareStats(
        avgCalories: Int,
        totalDays: Int,
        proteinG: Int,
        carbsG: Int,
        fatG: Int,
        currentWeight: Float,
        weightGoal: Float,
        streak: Int
    ) {
        val file = createStatsSummaryImage(
            title = "My Weekly Stats",
            avgCalories = avgCalories,
            totalDays = totalDays,
            proteinG = proteinG,
            carbsG = carbsG,
            fatG = fatG,
            currentWeight = currentWeight,
            weightGoal = weightGoal,
            streak = streak
        )
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(Intent.createChooser(intent, "Share Stats").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
