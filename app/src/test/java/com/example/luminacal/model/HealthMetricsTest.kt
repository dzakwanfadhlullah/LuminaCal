package com.example.luminacal.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for HealthMetrics model - BMR, TDEE, and macro calculations
 */
class HealthMetricsTest {

    // ==================== BMR CALCULATION TESTS ====================
    
    @Test
    fun `bmr calculation for male`() {
        val metrics = HealthMetrics(
            weight = 70f,
            height = 175f,
            age = 25,
            gender = Gender.MALE
        )
        // Mifflin-St Jeor: (10 × 70) + (6.25 × 175) - (5 × 25) + 5
        // = 700 + 1093.75 - 125 + 5 = 1673.75 ≈ 1673
        assertEquals(1673, metrics.bmr)
    }

    @Test
    fun `bmr calculation for female`() {
        val metrics = HealthMetrics(
            weight = 60f,
            height = 165f,
            age = 30,
            gender = Gender.FEMALE
        )
        // Mifflin-St Jeor: (10 × 60) + (6.25 × 165) - (5 × 30) - 161
        // = 600 + 1031.25 - 150 - 161 = 1320.25 ≈ 1320
        assertEquals(1320, metrics.bmr)
    }

    @Test
    fun `bmr calculation for other gender uses average`() {
        val metrics = HealthMetrics(
            weight = 65f,
            height = 170f,
            age = 28,
            gender = Gender.OTHER
        )
        // Mifflin-St Jeor: (10 × 65) + (6.25 × 170) - (5 × 28) - 78
        // = 650 + 1062.5 - 140 - 78 = 1494.5 ≈ 1494
        assertEquals(1494, metrics.bmr)
    }

    @Test
    fun `bmr increases with weight`() {
        val light = HealthMetrics(weight = 50f, height = 170f, age = 25, gender = Gender.MALE)
        val heavy = HealthMetrics(weight = 90f, height = 170f, age = 25, gender = Gender.MALE)
        
        assertTrue(heavy.bmr > light.bmr)
    }

    @Test
    fun `bmr increases with height`() {
        val short = HealthMetrics(weight = 70f, height = 160f, age = 25, gender = Gender.MALE)
        val tall = HealthMetrics(weight = 70f, height = 190f, age = 25, gender = Gender.MALE)
        
        assertTrue(tall.bmr > short.bmr)
    }

    @Test
    fun `bmr decreases with age`() {
        val young = HealthMetrics(weight = 70f, height = 175f, age = 20, gender = Gender.MALE)
        val old = HealthMetrics(weight = 70f, height = 175f, age = 60, gender = Gender.MALE)
        
        assertTrue(young.bmr > old.bmr)
    }

    @Test
    fun `male bmr is higher than female for same stats`() {
        val male = HealthMetrics(weight = 70f, height = 170f, age = 30, gender = Gender.MALE)
        val female = HealthMetrics(weight = 70f, height = 170f, age = 30, gender = Gender.FEMALE)
        
        assertTrue(male.bmr > female.bmr)
    }

    // ==================== TDEE CALCULATION TESTS ====================

    @Test
    fun `tdee is bmr times activity multiplier for sedentary`() {
        val metrics = HealthMetrics(
            weight = 70f,
            height = 175f,
            age = 25,
            gender = Gender.MALE,
            activityLevel = ActivityLevel.SEDENTARY
        )
        val expectedTdee = (metrics.bmr * 1.2f).toInt()
        assertEquals(expectedTdee, metrics.tdee)
    }

    @Test
    fun `tdee is bmr times activity multiplier for moderate`() {
        val metrics = HealthMetrics(
            weight = 70f,
            height = 175f,
            age = 25,
            gender = Gender.MALE,
            activityLevel = ActivityLevel.MODERATE
        )
        val expectedTdee = (metrics.bmr * 1.55f).toInt()
        assertEquals(expectedTdee, metrics.tdee)
    }

    @Test
    fun `tdee is bmr times activity multiplier for extra active`() {
        val metrics = HealthMetrics(
            weight = 70f,
            height = 175f,
            age = 25,
            gender = Gender.MALE,
            activityLevel = ActivityLevel.EXTRA_ACTIVE
        )
        val expectedTdee = (metrics.bmr * 1.9f).toInt()
        assertEquals(expectedTdee, metrics.tdee)
    }

    @Test
    fun `tdee increases with higher activity level`() {
        val sedentary = HealthMetrics(activityLevel = ActivityLevel.SEDENTARY)
        val active = HealthMetrics(activityLevel = ActivityLevel.ACTIVE)
        
        assertTrue(active.tdee > sedentary.tdee)
    }

    // ==================== TARGET CALORIES TESTS ====================

    @Test
    fun `targetCalories equals tdee for maintain goal`() {
        val metrics = HealthMetrics(
            fitnessGoal = FitnessGoal.MAINTAIN
        )
        assertEquals(metrics.tdee, metrics.targetCalories)
    }

    @Test
    fun `targetCalories is tdee minus 500 for weight loss`() {
        val metrics = HealthMetrics(
            weight = 100f,  // Higher weight to ensure we don't hit 1200 floor
            height = 180f,
            fitnessGoal = FitnessGoal.LOSE_WEIGHT
        )
        assertEquals(metrics.tdee - 500, metrics.targetCalories)
    }

    @Test
    fun `targetCalories is tdee plus 300 for muscle gain`() {
        val metrics = HealthMetrics(
            fitnessGoal = FitnessGoal.GAIN_MUSCLE
        )
        assertEquals(metrics.tdee + 300, metrics.targetCalories)
    }

    @Test
    fun `targetCalories has minimum of 1200`() {
        val metrics = HealthMetrics(
            weight = 40f,
            height = 150f,
            age = 70,
            gender = Gender.FEMALE,
            activityLevel = ActivityLevel.SEDENTARY,
            fitnessGoal = FitnessGoal.LOSE_WEIGHT
        )
        assertTrue(metrics.targetCalories >= 1200)
    }

    // ==================== MACRO RECOMMENDATIONS TESTS ====================

    @Test
    fun `recommendedProtein is 30 percent of calories divided by 4`() {
        val metrics = HealthMetrics()
        val expectedProtein = ((metrics.targetCalories * 0.30) / 4).toInt()
        assertEquals(expectedProtein, metrics.recommendedProtein)
    }

    @Test
    fun `recommendedCarbs is 40 percent of calories divided by 4`() {
        val metrics = HealthMetrics()
        val expectedCarbs = ((metrics.targetCalories * 0.40) / 4).toInt()
        assertEquals(expectedCarbs, metrics.recommendedCarbs)
    }

    @Test
    fun `recommendedFat is 30 percent of calories divided by 9`() {
        val metrics = HealthMetrics()
        val expectedFat = ((metrics.targetCalories * 0.30) / 9).toInt()
        assertEquals(expectedFat, metrics.recommendedFat)
    }

    @Test
    fun `macros increase proportionally with target calories`() {
        val maintenance = HealthMetrics(fitnessGoal = FitnessGoal.MAINTAIN)
        val bulking = HealthMetrics(fitnessGoal = FitnessGoal.GAIN_MUSCLE)
        
        assertTrue(bulking.recommendedProtein > maintenance.recommendedProtein)
        assertTrue(bulking.recommendedCarbs > maintenance.recommendedCarbs)
        assertTrue(bulking.recommendedFat > maintenance.recommendedFat)
    }

    // ==================== AVATAR SEED TESTS ====================

    @Test
    fun `avatarSeed removes spaces from username`() {
        val metrics = HealthMetrics(userName = "John Doe")
        assertEquals("JohnDoe", metrics.avatarSeed)
    }

    @Test
    fun `avatarSeed is truncated to 10 characters`() {
        val metrics = HealthMetrics(userName = "VeryLongUserNameThatExceedsTenCharacters")
        assertEquals(10, metrics.avatarSeed.length)
    }

    @Test
    fun `avatarSeed works with short names`() {
        val metrics = HealthMetrics(userName = "Jo")
        assertEquals("Jo", metrics.avatarSeed)
    }

    // ==================== DEFAULT VALUES TESTS ====================

    @Test
    fun `default values are sensible`() {
        val metrics = HealthMetrics()
        
        assertEquals("User", metrics.userName)
        assertEquals(70f, metrics.weight)
        assertEquals(170f, metrics.height)
        assertEquals(25, metrics.age)
        assertEquals(Gender.MALE, metrics.gender)
        assertEquals(ActivityLevel.MODERATE, metrics.activityLevel)
        assertEquals(FitnessGoal.MAINTAIN, metrics.fitnessGoal)
        assertEquals(2000, metrics.waterTargetMl)
    }

    @Test
    fun `default metrics produce reasonable values`() {
        val metrics = HealthMetrics()
        
        assertTrue(metrics.bmr in 1200..2500)
        assertTrue(metrics.tdee in 1500..4000)
        assertTrue(metrics.targetCalories in 1200..4500)
        assertTrue(metrics.recommendedProtein in 50..300)
        assertTrue(metrics.recommendedCarbs in 100..500)
        assertTrue(metrics.recommendedFat in 30..150)
    }
}
