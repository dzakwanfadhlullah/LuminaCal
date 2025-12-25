package com.example.luminacal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.luminacal.data.local.MealEntity
import com.example.luminacal.data.repository.HealthMetricsRepository
import com.example.luminacal.data.repository.MealRepository
import com.example.luminacal.data.repository.WaterRepository
import com.example.luminacal.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LuminaCalState(
    val calories: CalorieState = CalorieState(0, 2000),
    val macros: Macros = Macros(0, 0, 0),
    val history: List<HistoryEntry> = emptyList(),
    val darkMode: Boolean = false,
    val selectedTab: String = "home",
    val healthMetrics: HealthMetrics = HealthMetrics(),
    val water: WaterState = WaterState()
)

class MainViewModel(
    private val mealRepository: MealRepository,
    private val healthMetricsRepository: HealthMetricsRepository,
    private val waterRepository: WaterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LuminaCalState())
    val uiState: StateFlow<LuminaCalState> = _uiState.asStateFlow()

    init {
        // Load meals
        viewModelScope.launch {
            mealRepository.allMeals.collect { meals ->
                val history = meals.map { it.toHistoryEntry() }
                val totalCalories = history.sumOf { it.calories }
                val totalMacros = Macros(
                    protein = history.sumOf { it.macros.protein },
                    carbs = history.sumOf { it.macros.carbs },
                    fat = history.sumOf { it.macros.fat }
                )

                _uiState.update { state ->
                    state.copy(
                        history = history,
                        calories = state.calories.copy(consumed = totalCalories),
                        macros = totalMacros
                    )
                }
            }
        }

        // Load health metrics from database
        viewModelScope.launch {
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
        viewModelScope.launch {
            waterRepository.getWaterStateForToday().collect { waterState ->
                _uiState.update { state ->
                    state.copy(water = waterState)
                }
            }
        }
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
        viewModelScope.launch {
            healthMetricsRepository.saveHealthMetrics(metrics)
        }
    }

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            waterRepository.addWater(amountMl)
        }
    }

    fun addFood(name: String, calories: Int, macros: Macros, type: MealType) {
        viewModelScope.launch {
            val meal = MealEntity(
                name = name,
                time = "Just now",
                calories = calories,
                protein = macros.protein,
                carbs = macros.carbs,
                fat = macros.fat,
                type = type,
                date = System.currentTimeMillis()
            )
            mealRepository.insertMeal(meal)
        }
    }

    private fun MealEntity.toHistoryEntry() = HistoryEntry(
        id = id,
        name = name,
        time = time,
        calories = calories,
        macros = Macros(protein, carbs, fat),
        type = type
    )

    class Factory(
        private val mealRepository: MealRepository,
        private val healthMetricsRepository: HealthMetricsRepository,
        private val waterRepository: WaterRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(mealRepository, healthMetricsRepository, waterRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

