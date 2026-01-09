package com.example.luminacal.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.CompassCalibration
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector

import com.example.luminacal.R

sealed class Screen(val route: String, val labelRes: Int, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", R.string.nav_home, Icons.Default.Home)
    object Statistics : Screen("stats", R.string.nav_stats, Icons.AutoMirrored.Filled.TrendingUp)
    object Explore : Screen("explore", R.string.nav_explore, Icons.Default.Search)
    object Profile : Screen("profile", R.string.nav_profile, Icons.Default.Settings)
    object Camera : Screen("camera", R.string.nav_scan, Icons.Default.QrCodeScanner)
    object FoodDetail : Screen("food_detail/{foodName}/{calories}/{time}/{category}/{imageUrl}", R.string.nav_detail, Icons.Default.PieChart) {
        fun createRoute(foodName: String, calories: String, time: String, category: String, imageUrl: String): String {
            val encodedUrl = java.net.URLEncoder.encode(imageUrl, "UTF-8")
            val encodedName = java.net.URLEncoder.encode(foodName, "UTF-8")
            return "food_detail/$encodedName/$calories/$time/$category/$encodedUrl"
        }
    }
    object HealthMetrics : Screen("health_metrics", R.string.nav_health, Icons.Default.ShowChart)
    object ReminderSettings : Screen("reminder_settings", R.string.nav_reminders, Icons.Default.Settings)
    object Onboarding : Screen("onboarding", R.string.nav_home, Icons.Default.CompassCalibration)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Statistics,
    Screen.Camera, // Centered primary action
    Screen.Explore,
    Screen.Profile
)
