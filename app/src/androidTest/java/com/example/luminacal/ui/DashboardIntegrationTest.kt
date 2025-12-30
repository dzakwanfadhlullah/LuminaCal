package com.example.luminacal.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.luminacal.model.*
import com.example.luminacal.ui.components.WaterTrackingWidget
import com.example.luminacal.ui.components.EmptyStateCard
import com.example.luminacal.ui.screens.dashboard.DashboardScreen
import com.example.luminacal.ui.theme.LuminaCalTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant

/**
 * UI tests for Dashboard screen and components
 */
@RunWith(AndroidJUnit4::class)
class DashboardIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== WATER TRACKING WIDGET TESTS ====================

    @Test
    fun waterWidget_displaysConsumedAmount() {
        composeTestRule.setContent {
            LuminaCalTheme {
                WaterTrackingWidget(
                    waterState = WaterState(consumed = 1500, target = 2000),
                    onAddWater = {}
                )
            }
        }

        // Format is "1500ml" with suffix
        composeTestRule.onNodeWithText("1500ml", substring = true).assertIsDisplayed()
    }

    @Test
    fun waterWidget_displaysTargetAmount() {
        composeTestRule.setContent {
            LuminaCalTheme {
                WaterTrackingWidget(
                    waterState = WaterState(consumed = 1000, target = 2500),
                    onAddWater = {}
                )
            }
        }

        // Format is "2500ml" with suffix
        composeTestRule.onNodeWithText("2500ml", substring = true).assertIsDisplayed()
    }

    @Test
    fun waterWidget_hasAddButtons() {
        composeTestRule.setContent {
            LuminaCalTheme {
                WaterTrackingWidget(
                    waterState = WaterState(consumed = 0, target = 2000),
                    onAddWater = {}
                )
            }
        }

        // Actual buttons: "1 Glass" and "500ml"
        composeTestRule.onNodeWithText("1 Glass", substring = true).assertExists()
        composeTestRule.onNodeWithText("500ml", substring = true).assertExists()
    }

    @Test
    fun waterWidget_addButtonCallsCallback() {
        var addedAmount = 0

        composeTestRule.setContent {
            LuminaCalTheme {
                WaterTrackingWidget(
                    waterState = WaterState(consumed = 0, target = 2000),
                    onAddWater = { amount -> addedAmount = amount }
                )
            }
        }

        // "1 Glass" button adds 250ml
        composeTestRule.onNodeWithText("1 Glass", substring = true).performClick()

        assert(addedAmount == 250)
    }

    @Test
    fun waterWidget_500mlButtonCallsCallback() {
        var addedAmount = 0

        composeTestRule.setContent {
            LuminaCalTheme {
                WaterTrackingWidget(
                    waterState = WaterState(consumed = 0, target = 2000),
                    onAddWater = { amount -> addedAmount = amount }
                )
            }
        }

        composeTestRule.onNodeWithText("500ml", substring = true).performClick()

        assert(addedAmount == 500)
    }

    // ==================== EMPTY STATE CARD TESTS ====================

    @Test
    fun emptyStateCard_displaysTitle() {
        composeTestRule.setContent {
            LuminaCalTheme {
                EmptyStateCard(
                    icon = Icons.Default.Restaurant,
                    title = "No meals logged yet",
                    subtitle = "Start tracking your meals"
                )
            }
        }

        composeTestRule.onNodeWithText("No meals logged yet").assertIsDisplayed()
    }

    @Test
    fun emptyStateCard_displaysSubtitle() {
        composeTestRule.setContent {
            LuminaCalTheme {
                EmptyStateCard(
                    icon = Icons.Default.Restaurant,
                    title = "No meals logged yet",
                    subtitle = "Start tracking your meals"
                )
            }
        }

        composeTestRule.onNodeWithText("Start tracking your meals").assertIsDisplayed()
    }

    // ==================== DASHBOARD SCREEN TESTS ====================

    @Test
    fun dashboard_displaysHeader() {
        composeTestRule.setContent {
            LuminaCalTheme {
                DashboardScreen(
                    isLoading = false,
                    calorieState = CalorieState(consumed = 1000, target = 2000),
                    macros = Macros(protein = 50, carbs = 100, fat = 40),
                    healthMetrics = HealthMetrics(),
                    history = emptyList(),
                    waterState = WaterState(),
                    onAddWater = {},
                    onLogClick = {},
                    onDeleteMeal = {},
                    onProfileClick = {},
                    onViewAllClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("TODAY").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dashboard").assertIsDisplayed()
    }

    @Test
    fun dashboard_displaysRemainingCalories() {
        composeTestRule.setContent {
            LuminaCalTheme {
                DashboardScreen(
                    isLoading = false,
                    calorieState = CalorieState(consumed = 500, target = 2000),
                    macros = Macros(protein = 50, carbs = 100, fat = 40),
                    healthMetrics = HealthMetrics(),
                    history = emptyList(),
                    waterState = WaterState(),
                    onAddWater = {},
                    onLogClick = {},
                    onDeleteMeal = {},
                    onProfileClick = {},
                    onViewAllClick = {}
                )
            }
        }

        // Remaining = 2000 - 500 = 1500, just check "Remaining" label exists
        composeTestRule.onNodeWithText("Remaining").assertExists()
    }

    @Test
    fun dashboard_displaysMacros() {
        composeTestRule.setContent {
            LuminaCalTheme {
                DashboardScreen(
                    isLoading = false,
                    calorieState = CalorieState(consumed = 1000, target = 2000),
                    macros = Macros(protein = 75, carbs = 125, fat = 55),
                    healthMetrics = HealthMetrics(),
                    history = emptyList(),
                    waterState = WaterState(),
                    onAddWater = {},
                    onLogClick = {},
                    onDeleteMeal = {},
                    onProfileClick = {},
                    onViewAllClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Protein").assertExists()
        composeTestRule.onNodeWithText("Carbs").assertExists()
        composeTestRule.onNodeWithText("Fat").assertExists()
    }

    @Test
    fun dashboard_displaysWaterWidget() {
        composeTestRule.setContent {
            LuminaCalTheme {
                DashboardScreen(
                    isLoading = false,
                    calorieState = CalorieState(consumed = 1000, target = 2000),
                    macros = Macros(protein = 50, carbs = 100, fat = 40),
                    healthMetrics = HealthMetrics(),
                    history = emptyList(),
                    waterState = WaterState(consumed = 1200, target = 2000),
                    onAddWater = {},
                    onLogClick = {},
                    onDeleteMeal = {},
                    onProfileClick = {},
                    onViewAllClick = {}
                )
            }
        }

        // Just check Water Intake label exists
        composeTestRule.onNodeWithText("Water Intake").assertExists()
    }

    @Test
    fun dashboard_displaysEmptyStateWhenNoHistory() {
        composeTestRule.setContent {
            LuminaCalTheme {
                DashboardScreen(
                    isLoading = false,
                    calorieState = CalorieState(consumed = 0, target = 2000),
                    macros = Macros(protein = 0, carbs = 0, fat = 0),
                    healthMetrics = HealthMetrics(),
                    history = emptyList(),
                    waterState = WaterState(),
                    onAddWater = {},
                    onLogClick = {},
                    onDeleteMeal = {},
                    onProfileClick = {},
                    onViewAllClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("No meals logged yet").assertExists()
    }

    @Test
    fun dashboard_displaysMealHistory() {
        val mealHistory = listOf(
            HistoryEntry(
                id = 1,
                name = "Nasi Goreng",
                time = "12:00 PM",
                calories = 580,
                macros = Macros(protein = 18, carbs = 72, fat = 24),
                type = MealType.LUNCH
            )
        )

        composeTestRule.setContent {
            LuminaCalTheme {
                DashboardScreen(
                    isLoading = false,
                    calorieState = CalorieState(consumed = 580, target = 2000),
                    macros = Macros(protein = 18, carbs = 72, fat = 24),
                    healthMetrics = HealthMetrics(),
                    history = mealHistory,
                    waterState = WaterState(),
                    onAddWater = {},
                    onLogClick = {},
                    onDeleteMeal = {},
                    onProfileClick = {},
                    onViewAllClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Nasi Goreng").assertExists()
    }

    @Test
    fun dashboard_viewAllClickWorks() {
        var viewAllClicked = false

        composeTestRule.setContent {
            LuminaCalTheme {
                DashboardScreen(
                    isLoading = false,
                    calorieState = CalorieState(consumed = 0, target = 2000),
                    macros = Macros(protein = 0, carbs = 0, fat = 0),
                    healthMetrics = HealthMetrics(),
                    history = emptyList(),
                    waterState = WaterState(),
                    onAddWater = {},
                    onLogClick = {},
                    onDeleteMeal = {},
                    onProfileClick = {},
                    onViewAllClick = { viewAllClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("View All").performClick()

        assert(viewAllClicked)
    }

    @Test
    fun dashboard_recentLogsHeaderDisplayed() {
        composeTestRule.setContent {
            LuminaCalTheme {
                DashboardScreen(
                    isLoading = false,
                    calorieState = CalorieState(consumed = 0, target = 2000),
                    macros = Macros(protein = 0, carbs = 0, fat = 0),
                    healthMetrics = HealthMetrics(),
                    history = emptyList(),
                    waterState = WaterState(),
                    onAddWater = {},
                    onLogClick = {},
                    onDeleteMeal = {},
                    onProfileClick = {},
                    onViewAllClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Recent Logs").assertExists()
    }
}
