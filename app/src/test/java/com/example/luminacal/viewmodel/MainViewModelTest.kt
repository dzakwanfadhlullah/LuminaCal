package com.example.luminacal.viewmodel

import com.example.luminacal.data.repository.*
import com.example.luminacal.model.Macros
import com.example.luminacal.model.MealType
import com.example.luminacal.model.HistoryEntry
import com.example.luminacal.util.AppPreferences
import com.example.luminacal.util.MainDispatcherRule
import com.example.luminacal.data.local.MealEntity
import com.example.luminacal.model.HealthMetrics
import com.example.luminacal.model.WaterState
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainViewModel
    
    private val mealRepository = mockk<MealRepository>(relaxed = true)
    private val healthMetricsRepository = mockk<HealthMetricsRepository>(relaxed = true)
    private val waterRepository = mockk<WaterRepository>(relaxed = true)
    private val weightRepository = mockk<WeightRepository>(relaxed = true)
    private val customFoodRepository = mockk<CustomFoodRepository>(relaxed = true)
    private val appPreferences = mockk<AppPreferences>(relaxed = true)

    @Before
    fun setup() {
        // Mock default flow returns to avoid NPEs during init
        every { mealRepository.allMeals } returns MutableStateFlow(emptyList())
        every { healthMetricsRepository.healthMetrics } returns MutableStateFlow(HealthMetrics())
        every { waterRepository.getWaterStateForToday() } returns MutableStateFlow(WaterState())
        every { weightRepository.allWeights } returns MutableStateFlow(emptyList())
        every { weightRepository.weeklyTrend } returns MutableStateFlow(WeightTrend(null, null, null))
        every { weightRepository.weightStats } returns MutableStateFlow(WeightStats(null, null, null, null, null, null))
        every { customFoodRepository.allCustomFoods } returns MutableStateFlow(emptyList())
        every { customFoodRepository.getRecentFoods(any()) } returns MutableStateFlow(emptyList())
        
        viewModel = MainViewModel(
            mealRepository,
            healthMetricsRepository,
            waterRepository,
            weightRepository,
            customFoodRepository,
            appPreferences
        )
    }

    @Test
    fun `addFood calls repository insertMeal`() = runTest {
        val name = "Apple"
        val calories = 95
        val macros = Macros(0, 25, 0)
        val type = MealType.SNACK

        viewModel.addFood(name, calories, macros, type)

        coVerify { 
            mealRepository.insertMeal(match { 
                it.name == name && 
                it.calories == calories && 
                it.protein == macros.protein &&
                it.carbs == macros.carbs &&
                it.fat == macros.fat &&
                it.type == type
            }) 
        }
    }

    @Test
    fun `addWater calls repository addWater when valid`() = runTest {
        val amount = 250 // Valid amount
        
        viewModel.addWater(amount)
        
        coVerify { waterRepository.addWater(amount) }
    }

    @Test
    fun `addWater does not call repository when invalid`() = runTest {
        val amount = -50 // Invalid amount
        
        viewModel.addWater(amount)
        
        coVerify(exactly = 0) { waterRepository.addWater(any()) }
    }

    @Test
    fun `addWeight calls repository addWeight when valid`() = runTest {
        val weight = 70.5f
        
        viewModel.addWeight(weight)
        
        coVerify { weightRepository.addWeight(weight, null) }
    }

    @Test
    fun `deleteMeal calls repository deleteMeal`() = runTest {
        val entry = HistoryEntry(
            id = 1,
            name = "Test Meal",
            timestamp = 123456789,
            calories = 500,
            macros = Macros(20, 50, 20),
            type = MealType.LUNCH
        )
        
        viewModel.deleteMeal(entry)
        
        coVerify { 
            mealRepository.deleteMeal(match { 
                it.id == entry.id
            }) 
        }
    }
}
