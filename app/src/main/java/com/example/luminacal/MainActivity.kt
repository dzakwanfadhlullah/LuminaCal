package com.example.luminacal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.luminacal.ui.components.MeshBackground
import com.example.luminacal.ui.navigation.BottomNavigationBar
import com.example.luminacal.ui.navigation.Screen
import com.example.luminacal.ui.screens.dashboard.DashboardScreen
import com.example.luminacal.data.local.LuminaDatabase
import com.example.luminacal.data.repository.HealthMetricsRepository
import com.example.luminacal.data.repository.MealRepository
import com.example.luminacal.model.Macros
import com.example.luminacal.model.MealType
import com.example.luminacal.ui.theme.LuminaCalTheme
import com.example.luminacal.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = LuminaDatabase.getDatabase(this)
        val mealRepository = MealRepository(database.mealDao())
        val healthMetricsRepository = HealthMetricsRepository(database.healthMetricsDao())
        val waterRepository = com.example.luminacal.data.repository.WaterRepository(database.waterDao())
        val weightRepository = com.example.luminacal.data.repository.WeightRepository(database.weightDao())
        val factory = MainViewModel.Factory(mealRepository, healthMetricsRepository, waterRepository, weightRepository)

        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel(factory = factory)
            val state by viewModel.uiState.collectAsState()
            
            LuminaCalTheme(darkTheme = state.darkMode) {
                MainContent(viewModel)
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainContent(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        MeshBackground(darkMode = state.darkMode)
        
        SharedTransitionLayout {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                bottomBar = {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        onScanClick = {
                            navController.navigate(Screen.Camera.route)
                        }
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                calorieState = state.calories,
                                macros = state.macros,
                                history = state.history,
                                waterState = state.water,
                                onAddWater = { amount -> viewModel.addWater(amount) },
                                onLogClick = { entry ->
                                    navController.navigate(Screen.FoodDetail.route)
                                },
                                onProfileClick = {
                                    navController.navigate(Screen.Profile.route)
                                },
                                onViewAllClick = {
                                    navController.navigate(Screen.Statistics.route)
                                }
                            )
                        }
                        composable(Screen.Statistics.route) {
                            com.example.luminacal.ui.screens.statistics.StatisticsScreen()
                        }
                        composable(Screen.Explore.route) {
                            com.example.luminacal.ui.screens.explore.ExploreScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                onFoodClick = {
                                    navController.navigate(Screen.FoodDetail.route)
                                }
                            )
                        }
                        composable(Screen.Profile.route) {
                            com.example.luminacal.ui.screens.profile.ProfileScreen(
                                darkMode = state.darkMode,
                                onToggleDarkMode = { viewModel.toggleDarkMode() },
                                onHealthClick = { navController.navigate(Screen.HealthMetrics.route) }
                            )
                        }
                        composable(Screen.FoodDetail.route) {
                            com.example.luminacal.ui.screens.detail.FoodDetailScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                onBack = { navController.popBackStack() },
                                onLogMeal = { name, cals, macros, type ->
                                    viewModel.addFood(name, cals, macros, type)
                                }
                            )
                        }
                        composable(Screen.Camera.route) {
                            com.example.luminacal.ui.screens.camera.CameraScannerScreen(onClose = {
                                navController.popBackStack()
                            })
                        }
                        composable(Screen.HealthMetrics.route) {
                            com.example.luminacal.ui.screens.health.HealthMetricsScreen(
                                onBack = { navController.popBackStack() },
                                onApplyGoals = { metrics ->
                                    viewModel.updateHealthMetrics(metrics)
                                },
                                weightHistory = state.weightHistory,
                                weightTrend = state.weightTrend,
                                onAddWeight = { weight, note -> viewModel.addWeight(weight, note) },
                                onDeleteWeight = { entry -> viewModel.deleteWeight(entry) }
                            )
                        }
                    }
                }
            }
        }
    }
}