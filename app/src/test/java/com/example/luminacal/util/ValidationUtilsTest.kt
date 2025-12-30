package com.example.luminacal.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ValidationUtils
 */
class ValidationUtilsTest {

    // ==================== WEIGHT VALIDATION ====================
    
    @Test
    fun `validateWeight returns valid for normal weight`() {
        val result = ValidationUtils.validateWeight(70f)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
        assertNull(result.warningMessage)
    }

    @Test
    fun `validateWeight returns error for zero weight`() {
        val result = ValidationUtils.validateWeight(0f)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateWeight returns error for negative weight`() {
        val result = ValidationUtils.validateWeight(-5f)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateWeight returns error for weight below minimum`() {
        val result = ValidationUtils.validateWeight(15f)
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("minimum") == true)
    }

    @Test
    fun `validateWeight returns error for weight above maximum`() {
        val result = ValidationUtils.validateWeight(350f)
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("maksimum") == true)
    }

    @Test
    fun `validateWeight returns warning for very low weight`() {
        val result = ValidationUtils.validateWeight(35f)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }

    @Test
    fun `validateWeight returns warning for very high weight`() {
        val result = ValidationUtils.validateWeight(220f)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }

    // ==================== HEIGHT VALIDATION ====================

    @Test
    fun `validateHeight returns valid for normal height`() {
        val result = ValidationUtils.validateHeight(170f)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateHeight returns error for height below minimum`() {
        val result = ValidationUtils.validateHeight(90f)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateHeight returns error for height above maximum`() {
        val result = ValidationUtils.validateHeight(260f)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateHeight returns warning for very short height`() {
        val result = ValidationUtils.validateHeight(115f)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }

    // ==================== AGE VALIDATION ====================

    @Test
    fun `validateAge returns valid for normal age`() {
        val result = ValidationUtils.validateAge(25)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateAge returns error for age below minimum`() {
        val result = ValidationUtils.validateAge(5)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateAge returns error for age above maximum`() {
        val result = ValidationUtils.validateAge(130)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateAge returns warning for young age`() {
        val result = ValidationUtils.validateAge(12)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }

    @Test
    fun `validateAge returns warning for elderly age`() {
        val result = ValidationUtils.validateAge(85)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }

    // ==================== WATER INTAKE VALIDATION ====================

    @Test
    fun `validateWaterIntake returns valid for normal amount`() {
        val result = ValidationUtils.validateWaterIntake(250)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateWaterIntake returns error for zero amount`() {
        val result = ValidationUtils.validateWaterIntake(0)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateWaterIntake returns error for amount above maximum`() {
        val result = ValidationUtils.validateWaterIntake(6000)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateWaterIntake returns warning for very high single intake`() {
        val result = ValidationUtils.validateWaterIntake(3500)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }

    // ==================== CALORIES VALIDATION ====================

    @Test
    fun `validateCalories returns valid for normal calories`() {
        val result = ValidationUtils.validateCalories(500)
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateCalories returns valid for zero calories`() {
        val result = ValidationUtils.validateCalories(0)
        assertTrue(result.isValid)
    }

    @Test
    fun `validateCalories returns error for negative calories`() {
        val result = ValidationUtils.validateCalories(-100)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateCalories returns error for calories above maximum`() {
        val result = ValidationUtils.validateCalories(15000)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateCalories returns warning for very high calories`() {
        val result = ValidationUtils.validateCalories(4000)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }

    // ==================== FOOD NAME VALIDATION ====================

    @Test
    fun `validateFoodName returns valid for normal name`() {
        val result = ValidationUtils.validateFoodName("Nasi Goreng")
        assertTrue(result.isValid)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validateFoodName returns error for empty name`() {
        val result = ValidationUtils.validateFoodName("")
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateFoodName returns error for whitespace only name`() {
        val result = ValidationUtils.validateFoodName("   ")
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateFoodName returns error for name exceeding max length`() {
        val longName = "A".repeat(150)
        val result = ValidationUtils.validateFoodName(longName)
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("maksimum") == true)
    }

    // ==================== MACRO VALIDATION ====================

    @Test
    fun `validateMacro returns valid for normal value`() {
        val result = ValidationUtils.validateMacro(25, "Protein")
        assertTrue(result.isValid)
    }

    @Test
    fun `validateMacro returns error for negative value`() {
        val result = ValidationUtils.validateMacro(-10, "Protein")
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Protein") == true)
    }

    @Test
    fun `validateMacro returns error for value above maximum`() {
        val result = ValidationUtils.validateMacro(600, "Carbs")
        assertFalse(result.isValid)
        assertTrue(result.errorMessage?.contains("Carbs") == true)
    }

    // ==================== BMI VALIDATION ====================

    @Test
    fun `validateBMI returns valid for normal BMI`() {
        // BMI = 70 / (1.75)^2 = 22.86 (normal range)
        val result = ValidationUtils.validateBMI(70f, 175f)
        assertTrue(result.isValid)
        assertNull(result.warningMessage)
    }

    @Test
    fun `validateBMI returns warning for underweight`() {
        // BMI = 45 / (1.70)^2 = 15.57 (underweight)
        val result = ValidationUtils.validateBMI(45f, 170f)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
        assertTrue(result.warningMessage?.contains("kurus", ignoreCase = true) == true)
    }

    @Test
    fun `validateBMI returns warning for overweight`() {
        // BMI = 85 / (1.70)^2 = 29.41 (overweight)
        val result = ValidationUtils.validateBMI(85f, 170f)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }

    @Test
    fun `validateBMI returns warning for obese`() {
        // BMI = 120 / (1.70)^2 = 41.5 (obese class III)
        val result = ValidationUtils.validateBMI(120f, 170f)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
        assertTrue(result.warningMessage?.contains("Obesitas", ignoreCase = true) == true)
    }

    @Test
    fun `validateBMI returns error for invalid values`() {
        val result = ValidationUtils.validateBMI(0f, 170f)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    // ==================== TIMESTAMP VALIDATION ====================

    @Test
    fun `validateTimestamp returns valid for current time`() {
        val result = ValidationUtils.validateTimestamp(System.currentTimeMillis())
        assertTrue(result.isValid)
    }

    @Test
    fun `validateTimestamp returns valid for past time`() {
        val pastTime = System.currentTimeMillis() - 86400000 // 1 day ago
        val result = ValidationUtils.validateTimestamp(pastTime)
        assertTrue(result.isValid)
    }

    @Test
    fun `validateTimestamp returns error for future time`() {
        val futureTime = System.currentTimeMillis() + 3600000 // 1 hour in future
        val result = ValidationUtils.validateTimestamp(futureTime)
        assertFalse(result.isValid)
        assertNotNull(result.errorMessage)
    }

    @Test
    fun `validateTimestamp returns error for negative timestamp`() {
        val result = ValidationUtils.validateTimestamp(-1)
        assertFalse(result.isValid)
    }

    // ==================== DAILY WATER TOTAL VALIDATION ====================

    @Test
    fun `validateDailyWaterTotal returns valid for normal daily total`() {
        val result = ValidationUtils.validateDailyWaterTotal(2000)
        assertTrue(result.isValid)
        assertNull(result.warningMessage)
    }

    @Test
    fun `validateDailyWaterTotal returns warning for excessive daily total`() {
        val result = ValidationUtils.validateDailyWaterTotal(9000)
        assertTrue(result.isValid)
        assertNotNull(result.warningMessage)
    }
}
