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
import androidx.compose.runtime.remember
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
        
        // Initialize Notifications
        com.example.luminacal.util.NotificationHelper.createNotificationChannels(this)
        com.example.luminacal.util.ReminderScheduler.scheduleMealReminders(this)
        
        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            androidx.core.app.ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }

        val database = LuminaDatabase.getDatabase(this)
        val mealRepository = MealRepository(database.mealDao())
        val healthMetricsRepository = HealthMetricsRepository(database.healthMetricsDao())
        val waterRepository = com.example.luminacal.data.repository.WaterRepository(database.waterDao())
        val weightRepository = com.example.luminacal.data.repository.WeightRepository(database.weightDao())
        val factory = MainViewModel.Factory(mealRepository, healthMetricsRepository, waterRepository, weightRepository)
        
        // Check onboarding status
        val showOnboarding = !com.example.luminacal.util.OnboardingPrefs.isOnboardingComplete(this)

        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = viewModel(factory = factory)
            val state by viewModel.uiState.collectAsState()
            
            LuminaCalTheme(darkTheme = state.darkMode) {
                MainContent(
                    viewModel = viewModel,
                    showOnboarding = showOnboarding,
                    onOnboardingComplete = { metrics ->
                        viewModel.updateHealthMetrics(metrics)
                        com.example.luminacal.util.OnboardingPrefs.setOnboardingComplete(this@MainActivity)
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainContent(
    viewModel: MainViewModel,
    showOnboarding: Boolean = false,
    onOnboardingComplete: (com.example.luminacal.model.HealthMetrics) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Snackbar for error messages
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    
    // Show error as snackbar
    androidx.compose.runtime.LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }
    
    // Determine start destination based on onboarding status
    val startDestination = if (showOnboarding) Screen.Onboarding.route else Screen.Dashboard.route

    Box(modifier = Modifier.fillMaxSize()) {
        MeshBackground(darkMode = state.darkMode)
        
        SharedTransitionLayout {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                snackbarHost = { 
                    androidx.compose.material3.SnackbarHost(snackbarHostState) 
                },
                bottomBar = {
                    // Hide bottom bar during onboarding
                    if (currentRoute != Screen.Onboarding.route) {
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
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        // Onboarding Screen
                        composable(Screen.Onboarding.route) {
                            com.example.luminacal.ui.screens.onboarding.OnboardingScreen(
                                onComplete = { metrics ->
                                    onOnboardingComplete(metrics)
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                                    }
                                },
                                onSkip = {
                                    navController.navigate(Screen.Dashboard.route) {
                                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                isLoading = state.isLoading,
                                calorieState = state.calories,
                                macros = state.macros,
                                healthMetrics = state.healthMetrics,
                                history = state.history,
                                waterState = state.water,
                                onAddWater = { amount -> viewModel.addWater(amount) },
                                onLogClick = { entry ->
                                    navController.navigate(Screen.FoodDetail.route)
                                },
                                onDeleteMeal = { entry ->
                                    viewModel.deleteMeal(entry)
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
                            com.example.luminacal.ui.screens.statistics.StatisticsScreen(
                                weeklyCalories = state.weeklyCalories,
                                weightPoints = state.weightPoints,
                                macros = state.macros,
                                weightGoal = when (state.healthMetrics.fitnessGoal) {
                                    com.example.luminacal.model.FitnessGoal.LOSE_WEIGHT -> state.healthMetrics.weight - 5f
                                    com.example.luminacal.model.FitnessGoal.GAIN_MUSCLE -> state.healthMetrics.weight + 5f
                                    else -> state.healthMetrics.weight
                                }
                            )
                        }
                        composable(Screen.Explore.route) {
                            com.example.luminacal.ui.screens.explore.ExploreScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                onFoodClick = { recipe ->
                                    navController.navigate(
                                        Screen.FoodDetail.createRoute(
                                            foodName = recipe.name,
                                            calories = recipe.calories,
                                            time = recipe.time,
                                            category = recipe.category,
                                            imageUrl = recipe.imageUrl
                                        )
                                    )
                                },
                                onManualAdd = { name, calories, macros, type ->
                                    viewModel.addFood(name, calories, macros, type)
                                }
                            )
                        }
                        composable(Screen.Profile.route) {
                            val context = androidx.compose.ui.platform.LocalContext.current
                            val dataExporter = remember { com.example.luminacal.util.DataExporter(context) }
                            com.example.luminacal.ui.screens.profile.ProfileScreen(
                                healthMetrics = state.healthMetrics,
                                darkMode = state.darkMode,
                                onToggleDarkMode = { viewModel.toggleDarkMode() },
                                onHealthClick = { navController.navigate(Screen.HealthMetrics.route) },
                                onExportCSV = {
                                    val mealsToExport = state.history.map { entry ->
                                        com.example.luminacal.data.local.MealEntity(
                                            id = entry.id,
                                            name = entry.name,
                                            time = "",
                                            calories = entry.calories,
                                            protein = entry.macros.protein,
                                            carbs = entry.macros.carbs,
                                            fat = entry.macros.fat,
                                            type = entry.type,
                                            date = System.currentTimeMillis()
                                        )
                                    }
                                    val file = dataExporter.exportMealsToCSV(mealsToExport)
                                    dataExporter.shareFile(file, "text/csv")
                                },
                                onExportJSON = {
                                    val mealsToExport = state.history.map { entry ->
                                        com.example.luminacal.data.local.MealEntity(
                                            id = entry.id,
                                            name = entry.name,
                                            time = "",
                                            calories = entry.calories,
                                            protein = entry.macros.protein,
                                            carbs = entry.macros.carbs,
                                            fat = entry.macros.fat,
                                            type = entry.type,
                                            date = System.currentTimeMillis()
                                        )
                                    }
                                    val file = dataExporter.exportToJSON(mealsToExport, state.healthMetrics, state.weightHistory)
                                    dataExporter.shareFile(file, "application/json")
                                },
                                onClearData = {
                                    viewModel.clearAllData()
                                }
                            )
                        }
                        composable(
                            route = Screen.FoodDetail.route,
                            arguments = listOf(
                                androidx.navigation.navArgument("foodName") { type = androidx.navigation.NavType.StringType },
                                androidx.navigation.navArgument("calories") { type = androidx.navigation.NavType.StringType },
                                androidx.navigation.navArgument("time") { type = androidx.navigation.NavType.StringType },
                                androidx.navigation.navArgument("category") { type = androidx.navigation.NavType.StringType },
                                androidx.navigation.navArgument("imageUrl") { type = androidx.navigation.NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val foodName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("foodName") ?: "", "UTF-8")
                            val calories = backStackEntry.arguments?.getString("calories") ?: "0 kcal"
                            val time = backStackEntry.arguments?.getString("time") ?: ""
                            val category = backStackEntry.arguments?.getString("category") ?: ""
                            val imageUrl = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("imageUrl") ?: "", "UTF-8")
                            
                            com.example.luminacal.ui.screens.detail.FoodDetailScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@composable,
                                foodName = foodName,
                                calories = calories,
                                time = time,
                                category = category,
                                imageUrl = imageUrl,
                                onBack = { navController.popBackStack() },
                                onLogMeal = { name, cals, macros, type ->
                                    viewModel.addFood(name, cals, macros, type)
                                }
                            )
                        }
                        composable(Screen.Camera.route) {
                            com.example.luminacal.ui.screens.camera.CameraScannerScreen(
                                onClose = {
                                    navController.popBackStack()
                                },
                                onFoodConfirmed = { nutritionInfo ->
                                    val macros = com.example.luminacal.model.Macros(
                                        protein = nutritionInfo.protein,
                                        carbs = nutritionInfo.carbs,
                                        fat = nutritionInfo.fat
                                    )
                                    viewModel.addFood(
                                        name = nutritionInfo.name,
                                        calories = nutritionInfo.calories,
                                        macros = macros,
                                        type = com.example.luminacal.model.MealType.LUNCH
                                    )
                                }
                            )
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