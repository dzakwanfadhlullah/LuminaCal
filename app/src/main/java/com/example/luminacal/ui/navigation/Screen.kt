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

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object Statistics : Screen("stats", "Stats", Icons.AutoMirrored.Filled.TrendingUp)
    object Explore : Screen("explore", "Explore", Icons.Default.Search)
    object Profile : Screen("profile", "Profile", Icons.Default.Settings)
    object Camera : Screen("camera", "Scan", Icons.Default.QrCodeScanner)
    object FoodDetail : Screen("food_detail/{foodName}/{calories}/{time}/{category}/{imageUrl}", "Detail", Icons.Default.PieChart) {
        fun createRoute(foodName: String, calories: String, time: String, category: String, imageUrl: String): String {
            val encodedUrl = java.net.URLEncoder.encode(imageUrl, "UTF-8")
            val encodedName = java.net.URLEncoder.encode(foodName, "UTF-8")
            return "food_detail/$encodedName/$calories/$time/$category/$encodedUrl"
        }
    }
    object HealthMetrics : Screen("health_metrics", "Health", Icons.Default.ShowChart)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Default.CompassCalibration)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Statistics,
    Screen.Camera, // Centered primary action
    Screen.Explore,
    Screen.Profile
)
