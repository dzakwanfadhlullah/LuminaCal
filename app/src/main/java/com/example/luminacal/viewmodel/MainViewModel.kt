package com.example.luminacal.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.luminacal.data.local.MealEntity
import com.example.luminacal.data.repository.HealthMetricsRepository
import com.example.luminacal.data.repository.MealRepository
import com.example.luminacal.data.repository.WaterRepository
import com.example.luminacal.data.repository.WeightRepository
import com.example.luminacal.data.repository.WeightEntry
import com.example.luminacal.data.repository.WeightTrend
import com.example.luminacal.model.*
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
    val weeklyCalories: List<com.example.luminacal.ui.components.charts.DailyCalories> = emptyList(),
    val weightPoints: List<com.example.luminacal.ui.components.charts.WeightPoint> = emptyList()
)

class MainViewModel(
    private val mealRepository: MealRepository,
    private val healthMetricsRepository: HealthMetricsRepository,
    private val waterRepository: WaterRepository,
    private val weightRepository: WeightRepository
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
        // Load meals and calculate daily/weekly stats
        viewModelScope.launch(exceptionHandler) {
            mealRepository.allMeals.collect { meals ->
                val history = meals.map { it.toHistoryEntry() }
                
                // Get today's start/end for consumed/macros
                val calendar = java.util.Calendar.getInstance()
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                val startOfToday = calendar.timeInMillis
                
                val todayMeals = meals.filter { it.date >= startOfToday }
                val totalCalories = todayMeals.sumOf { it.calories }
                val totalMacros = Macros(
                    protein = todayMeals.sumOf { it.protein },
                    carbs = todayMeals.sumOf { it.carbs },
                    fat = todayMeals.sumOf { it.fat }
                )

                // Calculate weekly calories
                val weekly = mutableListOf<com.example.luminacal.ui.components.charts.DailyCalories>()
                val sdf = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
                
                for (i in 6 downTo 0) {
                    val cal = java.util.Calendar.getInstance()
                    cal.add(java.util.Calendar.DAY_OF_YEAR, -i)
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
                    val start = cal.timeInMillis
                    cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
                    val end = cal.timeInMillis
                    
                    val dayCalories = meals.filter { it.date in start..end }.sumOf { it.calories }
                    weekly.add(
                        com.example.luminacal.ui.components.charts.DailyCalories(
                            day = sdf.format(cal.time),
                            calories = dayCalories.toFloat(),
                            target = _uiState.value.healthMetrics.targetCalories.toFloat()
                        )
                    )
                }

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        history = history,
                        calories = state.calories.copy(consumed = totalCalories),
                        macros = totalMacros,
                        weeklyCalories = weekly
                    )
                }
            }
        }

        // Load health metrics from database
        viewModelScope.launch(exceptionHandler) {
            healthMetricsRepository.healthMetrics.collect { savedMetrics ->
                if (savedMetrics != null) {
                    _uiState.update { state ->
                        state.copy(
                            healthMetrics = savedMetrics,
                            calories = state.calories.copy(target = savedMetrics.targetCalories)
                        )
                    }
                }
            }
        }

        // Load water state for today
        viewModelScope.launch(exceptionHandler) {
            waterRepository.getWaterStateForToday().collect { waterState ->
                _uiState.update { state ->
                    state.copy(water = waterState)
                }
            }
        }

        // Load weight history and calculate points
        viewModelScope.launch(exceptionHandler) {
            weightRepository.allWeights.collect { weights ->
                val points = weights.take(30).reversed().map {
                    com.example.luminacal.ui.components.charts.WeightPoint(
                        date = "", // We'll just use index for now in chart
                        weight = it.weightKg
                    )
                }
                _uiState.update { state ->
                    state.copy(
                        weightHistory = weights,
                        weightPoints = points
                    )
                }
            }
        }

        // Load weight trend
        viewModelScope.launch(exceptionHandler) {
            weightRepository.weeklyTrend.collect { trend ->
                _uiState.update { state ->
                    state.copy(weightTrend = trend)
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun setTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleDarkMode() {
        _uiState.update { it.copy(darkMode = !it.darkMode) }
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

    class Factory(
        private val mealRepository: MealRepository,
        private val healthMetricsRepository: HealthMetricsRepository,
        private val waterRepository: WaterRepository,
        private val weightRepository: WeightRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(mealRepository, healthMetricsRepository, waterRepository, weightRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


