package com.example.luminacal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.luminacal.data.local.MealEntity
import com.example.luminacal.data.local.CustomFoodEntity
import com.example.luminacal.data.repository.CustomFoodRepository
import com.example.luminacal.data.repository.HealthMetricsRepository
import com.example.luminacal.data.repository.MealRepository
import com.example.luminacal.data.repository.WaterRepository
import com.example.luminacal.data.repository.WeightRepository
import com.example.luminacal.data.repository.WeightEntry
import com.example.luminacal.data.repository.WeightStats
import com.example.luminacal.data.repository.WeightTrend
import com.example.luminacal.model.*
import com.example.luminacal.util.AppPreferences
import com.example.luminacal.util.ValidationUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LuminaCalState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val calories: CalorieState = CalorieState(0, 2000),
    val macros: Macros = Macros(0, 0, 0),
    val history: List<HistoryEntry> = emptyList(),
    val darkMode: Boolean = false,
    val selectedTab: String = "home",
    val healthMetrics: HealthMetrics = HealthMetrics(),
    val water: WaterState = WaterState(),
    val weightHistory: List<WeightEntry> = emptyList(),
    val weightTrend: WeightTrend = WeightTrend(null, null, null),
    val weightStats: WeightStats = WeightStats(null, null, null, null, null, null),
    val weeklyCalories: List<DailyCalories> = emptyList(),
    val weightPoints: List<WeightPoint> = emptyList(),
    val loggingStreak: Int = 0,
    val customFoods: List<CustomFoodEntity> = emptyList(),
    val recentCustomFoods: List<CustomFoodEntity> = emptyList()
)

class MainViewModel(
    private val mealRepository: MealRepository,
    private val healthMetricsRepository: HealthMetricsRepository,
    private val waterRepository: WaterRepository,
    private val weightRepository: WeightRepository,
    private val customFoodRepository: CustomFoodRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    
    companion object {
        private const val TAG = "MainViewModel"
    }
    
    private val _uiState = MutableStateFlow(LuminaCalState())
    val uiState: StateFlow<LuminaCalState> = _uiState.asStateFlow()
    
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "Coroutine exception", throwable)
        _uiState.update { it.copy(
            isLoading = false,
            errorMessage = "Something went wrong. Please try again."
        ) }
    }

    init {
        // Load dark mode preference from SharedPreferences
        _uiState.update { it.copy(darkMode = appPreferences.darkMode) }
        
        // OPTIMIZED: Combined primary data flows (meals + health metrics + water)
        // This reduces 7 coroutines to 3 main collections
        viewModelScope.launch(exceptionHandler) {
            combine(
                mealRepository.allMeals,
                healthMetricsRepository.healthMetrics,
                waterRepository.getWaterStateForToday()
            ) { meals, savedMetrics, waterState ->
                Triple(meals, savedMetrics, waterState)
            }.collect { (meals, savedMetrics, waterState) ->
                // Calculate today's data
                val todayData = calculateTodayData(meals)
                
                // Calculate weekly calories (moved to helper function)
                val weekly = calculateWeeklyCalories(meals, savedMetrics?.targetCalories ?: 2000)
                
                // Update state once with all primary data
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        history = meals.map { it.toHistoryEntry() },
                        calories = CalorieState(
                            consumed = todayData.calories,
                            target = savedMetrics?.targetCalories ?: state.calories.target
                        ),
                        macros = todayData.macros,
                        weeklyCalories = weekly,
                        healthMetrics = savedMetrics ?: state.healthMetrics,
                        water = waterState
                    )
                }
            }
        }

        // OPTIMIZED: Combined weight flows (history + trend + stats)
        viewModelScope.launch(exceptionHandler) {
            combine(
                weightRepository.allWeights,
                weightRepository.weeklyTrend,
                weightRepository.weightStats
            ) { weights, trend, stats ->
                Triple(weights, trend, stats)
            }.collect { (weights, trend, stats) ->
                val points = weights.take(30).reversed().map {
                    WeightPoint(date = "", weight = it.weightKg)
                }
                
                _uiState.update { state ->
                    state.copy(
                        weightHistory = weights,
                        weightPoints = points,
                        weightTrend = trend,
                        weightStats = stats
                    )
                }
            }
        }
        
        // Load logging streak (one-time, not a flow)
        viewModelScope.launch(exceptionHandler) {
            val streak = mealRepository.getLoggingStreak()
            _uiState.update { state ->
                state.copy(loggingStreak = streak)
            }
        }
    }
    
    /**
     * Helper: Calculate today's calories and macros
     */
    private fun calculateTodayData(meals: List<MealEntity>): TodayData {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        val startOfToday = calendar.timeInMillis
        
        val todayMeals = meals.filter { it.date >= startOfToday }
        return TodayData(
            calories = todayMeals.sumOf { it.calories },
            macros = Macros(
                protein = todayMeals.sumOf { it.protein },
                carbs = todayMeals.sumOf { it.carbs },
                fat = todayMeals.sumOf { it.fat }
            )
        )
    }
    
    /**
     * Helper: Calculate weekly calorie data for charts
     * Moved out of collect{} to avoid recalculation on every emission
     */
    private fun calculateWeeklyCalories(meals: List<MealEntity>, targetCalories: Int): List<DailyCalories> {
        val weekly = mutableListOf<DailyCalories>()
        val sdf = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
        
        for (i in 6 downTo 0) {
            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
            cal.set(java.util.Calendar.MINUTE, 0)
            cal.set(java.util.Calendar.SECOND, 0)
            val start = cal.timeInMillis
            
            cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
            cal.set(java.util.Calendar.MINUTE, 59)
            val end = cal.timeInMillis
            
            val dayCalories = meals.filter { it.date in start..end }.sumOf { it.calories }
            weekly.add(
                DailyCalories(
                    day = sdf.format(cal.time),
                    calories = dayCalories.toFloat(),
                    target = targetCalories.toFloat()
                )
            )
        }
        return weekly
    }
    
    private data class TodayData(val calories: Int, val macros: Macros)
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun setTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleDarkMode() {
        val newDarkMode = !_uiState.value.darkMode
        appPreferences.darkMode = newDarkMode  // Persist to SharedPreferences
        _uiState.update { it.copy(darkMode = newDarkMode) }
    }

    fun updateHealthMetrics(metrics: HealthMetrics) {
        _uiState.update { state ->
            state.copy(
                healthMetrics = metrics,
                calories = state.calories.copy(target = metrics.targetCalories)
            )
        }
        // Persist to database
        viewModelScope.launch(exceptionHandler) {
            healthMetricsRepository.saveHealthMetrics(metrics)
        }
    }

    fun addWater(amountMl: Int) {
        // Validate water intake
        val validation = ValidationUtils.validateWaterIntake(amountMl)
        if (!validation.isValid) {
            _uiState.update { it.copy(errorMessage = validation.errorMessage) }
            return
        }
        
        // Check daily total warning
        val currentTotal = _uiState.value.water.consumed
        val dailyValidation = ValidationUtils.validateDailyWaterTotal(currentTotal + amountMl)
        if (dailyValidation.warningMessage != null) {
            Log.w(TAG, dailyValidation.warningMessage)
        }
        
        viewModelScope.launch {
            waterRepository.addWater(amountMl)
        }
    }

    fun addWeight(weightKg: Float, note: String? = null) {
        // Validate weight
        val validation = ValidationUtils.validateWeight(weightKg)
        if (!validation.isValid) {
            _uiState.update { it.copy(errorMessage = validation.errorMessage) }
            return
        }
        
        viewModelScope.launch {
            weightRepository.addWeight(weightKg, note)
        }
    }

    fun deleteWeight(entry: WeightEntry) {
        viewModelScope.launch {
            weightRepository.deleteWeight(entry)
        }
    }

    fun addFood(name: String, calories: Int, macros: Macros, type: MealType) {
        // Validate food name
        val nameValidation = ValidationUtils.validateFoodName(name)
        if (!nameValidation.isValid) {
            _uiState.update { it.copy(errorMessage = nameValidation.errorMessage) }
            return
        }
        
        // Validate calories
        val caloriesValidation = ValidationUtils.validateCalories(calories)
        if (!caloriesValidation.isValid) {
            _uiState.update { it.copy(errorMessage = caloriesValidation.errorMessage) }
            return
        }
        
        // Validate macros
        val proteinValidation = ValidationUtils.validateMacro(macros.protein, "Protein")
        if (!proteinValidation.isValid) {
            _uiState.update { it.copy(errorMessage = proteinValidation.errorMessage) }
            return
        }
        
        val carbsValidation = ValidationUtils.validateMacro(macros.carbs, "Carbs")
        if (!carbsValidation.isValid) {
            _uiState.update { it.copy(errorMessage = carbsValidation.errorMessage) }
            return
        }
        
        val fatValidation = ValidationUtils.validateMacro(macros.fat, "Fat")
        if (!fatValidation.isValid) {
            _uiState.update { it.copy(errorMessage = fatValidation.errorMessage) }
            return
        }
        
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val meal = MealEntity(
                name = name.trim(),
                time = "", // Deprecated, using date instead
                calories = calories,
                protein = macros.protein,
                carbs = macros.carbs,
                fat = macros.fat,
                type = type,
                date = currentTime
            )
            mealRepository.insertMeal(meal)
        }
    }
    
    fun deleteMeal(entry: HistoryEntry) {
        viewModelScope.launch(exceptionHandler) {
            val meal = MealEntity(
                id = entry.id,
                name = entry.name,
                time = "",
                calories = entry.calories,
                protein = entry.macros.protein,
                carbs = entry.macros.carbs,
                fat = entry.macros.fat,
                type = entry.type,
                date = 0L // Not used for deletion
            )
            mealRepository.deleteMeal(meal)
        }
    }
    
    fun clearAllData() {
        viewModelScope.launch(exceptionHandler) {
            mealRepository.deleteAllMeals()
            waterRepository.clearAllWater()
            weightRepository.deleteAllWeights()
        }
    }

    // formatMealTime removed - localization moved to UI

    private fun MealEntity.toHistoryEntry() = HistoryEntry(
        id = id,
        name = name,
        timestamp = date,
        calories = calories,
        macros = Macros(protein, carbs, fat),
        type = type
    )
    
    // ============== CUSTOM FOODS ==============
    
    init {
        // Load custom foods
        viewModelScope.launch(exceptionHandler) {
            customFoodRepository.allCustomFoods.collect { foods ->
                _uiState.update { it.copy(customFoods = foods) }
            }
        }
        viewModelScope.launch(exceptionHandler) {
            customFoodRepository.getRecentFoods(5).collect { foods ->
                _uiState.update { it.copy(recentCustomFoods = foods) }
            }
        }
    }
    
    fun saveCustomFood(
        name: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        servingSize: String = "1 serving"
    ) {
        viewModelScope.launch(exceptionHandler) {
            customFoodRepository.saveCustomFood(name, calories, protein, carbs, fat, servingSize)
        }
    }
    
    fun deleteCustomFood(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            customFoodRepository.deleteCustomFood(id)
        }
    }
    
    fun toggleCustomFoodFavorite(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            customFoodRepository.toggleFavorite(id)
        }
    }
    
    fun incrementCustomFoodUseCount(id: Long) {
        viewModelScope.launch(exceptionHandler) {
            customFoodRepository.incrementUseCount(id)
        }
    }

    class Factory(
        private val mealRepository: MealRepository,
        private val healthMetricsRepository: HealthMetricsRepository,
        private val waterRepository: WaterRepository,
        private val weightRepository: WeightRepository,
        private val customFoodRepository: CustomFoodRepository,
        private val appPreferences: AppPreferences
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(mealRepository, healthMetricsRepository, waterRepository, weightRepository, customFoodRepository, appPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


