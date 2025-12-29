package com.example.luminacal.util

/**
 * Result of a validation check
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val warningMessage: String? = null
) {
    companion object {
        fun valid() = ValidationResult(isValid = true)
        fun error(message: String) = ValidationResult(isValid = false, errorMessage = message)
        fun warning(message: String) = ValidationResult(isValid = true, warningMessage = message)
    }
}

/**
 * Utility object for validating user inputs across the app
 */
object ValidationUtils {

    // Weight validation constants
    private const val WEIGHT_MIN_KG = 20f
    private const val WEIGHT_MAX_KG = 300f
    private const val WEIGHT_WARNING_LOW_KG = 40f
    private const val WEIGHT_WARNING_HIGH_KG = 200f

    // Height validation constants
    private const val HEIGHT_MIN_CM = 100f
    private const val HEIGHT_MAX_CM = 250f
    private const val HEIGHT_WARNING_LOW_CM = 120f
    private const val HEIGHT_WARNING_HIGH_CM = 220f

    // Age validation constants
    private const val AGE_MIN = 10
    private const val AGE_MAX = 120
    private const val AGE_WARNING_LOW = 15
    private const val AGE_WARNING_HIGH = 80

    // Water intake validation constants
    private const val WATER_MIN_ML = 1
    private const val WATER_MAX_ML = 5000
    private const val WATER_DAILY_MAX_ML = 8000
    private const val WATER_WARNING_HIGH_ML = 3000

    // Calories validation constants
    private const val CALORIES_MIN = 0
    private const val CALORIES_MAX = 10000
    private const val CALORIES_WARNING_HIGH = 3000

    // Food name validation
    private const val FOOD_NAME_MIN_LENGTH = 1
    private const val FOOD_NAME_MAX_LENGTH = 100

    /**
     * Validate weight in kilograms
     */
    fun validateWeight(kg: Float): ValidationResult {
        return when {
            kg <= 0 -> ValidationResult.error("Berat harus lebih dari 0 kg")
            kg < WEIGHT_MIN_KG -> ValidationResult.error("Berat minimum ${WEIGHT_MIN_KG.toInt()} kg")
            kg > WEIGHT_MAX_KG -> ValidationResult.error("Berat maksimum ${WEIGHT_MAX_KG.toInt()} kg")
            kg < WEIGHT_WARNING_LOW_KG -> ValidationResult.warning("Berat ${kg.toInt()} kg sangat rendah")
            kg > WEIGHT_WARNING_HIGH_KG -> ValidationResult.warning("Berat ${kg.toInt()} kg sangat tinggi")
            else -> ValidationResult.valid()
        }
    }

    /**
     * Validate height in centimeters
     */
    fun validateHeight(cm: Float): ValidationResult {
        return when {
            cm <= 0 -> ValidationResult.error("Tinggi harus lebih dari 0 cm")
            cm < HEIGHT_MIN_CM -> ValidationResult.error("Tinggi minimum ${HEIGHT_MIN_CM.toInt()} cm")
            cm > HEIGHT_MAX_CM -> ValidationResult.error("Tinggi maksimum ${HEIGHT_MAX_CM.toInt()} cm")
            cm < HEIGHT_WARNING_LOW_CM -> ValidationResult.warning("Tinggi ${cm.toInt()} cm di bawah rata-rata")
            cm > HEIGHT_WARNING_HIGH_CM -> ValidationResult.warning("Tinggi ${cm.toInt()} cm di atas rata-rata")
            else -> ValidationResult.valid()
        }
    }

    /**
     * Validate age in years
     */
    fun validateAge(years: Int): ValidationResult {
        return when {
            years < AGE_MIN -> ValidationResult.error("Umur minimum $AGE_MIN tahun")
            years > AGE_MAX -> ValidationResult.error("Umur maksimum $AGE_MAX tahun")
            years < AGE_WARNING_LOW -> ValidationResult.warning("Aplikasi ini dirancang untuk pengguna $AGE_WARNING_LOW+ tahun")
            years > AGE_WARNING_HIGH -> ValidationResult.warning("Konsultasi dokter disarankan untuk umur $years tahun")
            else -> ValidationResult.valid()
        }
    }

    /**
     * Validate single water intake in milliliters
     */
    fun validateWaterIntake(ml: Int): ValidationResult {
        return when {
            ml <= 0 -> ValidationResult.error("Jumlah air harus lebih dari 0 ml")
            ml < WATER_MIN_ML -> ValidationResult.error("Jumlah air minimum $WATER_MIN_ML ml")
            ml > WATER_MAX_ML -> ValidationResult.error("Jumlah air maksimum $WATER_MAX_ML ml per intake")
            ml > WATER_WARNING_HIGH_ML -> ValidationResult.warning("$ml ml sangat banyak untuk sekali minum")
            else -> ValidationResult.valid()
        }
    }

    /**
     * Validate total daily water intake
     */
    fun validateDailyWaterTotal(totalMl: Int): ValidationResult {
        return when {
            totalMl > WATER_DAILY_MAX_ML -> ValidationResult.warning("Total air harian ${totalMl}ml melebihi batas aman")
            else -> ValidationResult.valid()
        }
    }

    /**
     * Validate calorie amount
     */
    fun validateCalories(kcal: Int): ValidationResult {
        return when {
            kcal < CALORIES_MIN -> ValidationResult.error("Kalori tidak boleh negatif")
            kcal > CALORIES_MAX -> ValidationResult.error("Kalori maksimum $CALORIES_MAX kcal")
            kcal > CALORIES_WARNING_HIGH -> ValidationResult.warning("$kcal kcal sangat tinggi untuk satu makanan")
            else -> ValidationResult.valid()
        }
    }

    /**
     * Validate macro values (protein, carbs, fat in grams)
     */
    fun validateMacro(grams: Int, macroName: String): ValidationResult {
        return when {
            grams < 0 -> ValidationResult.error("$macroName tidak boleh negatif")
            grams > 500 -> ValidationResult.error("$macroName maksimum 500g")
            else -> ValidationResult.valid()
        }
    }

    /**
     * Validate food name
     */
    fun validateFoodName(name: String): ValidationResult {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> ValidationResult.error("Nama makanan tidak boleh kosong")
            trimmed.length < FOOD_NAME_MIN_LENGTH -> ValidationResult.error("Nama makanan terlalu pendek")
            trimmed.length > FOOD_NAME_MAX_LENGTH -> ValidationResult.error("Nama makanan maksimum $FOOD_NAME_MAX_LENGTH karakter")
            else -> ValidationResult.valid()
        }
    }

    /**
     * Calculate BMI and return validation result with health category
     */
    fun validateBMI(weightKg: Float, heightCm: Float): ValidationResult {
        if (weightKg <= 0 || heightCm <= 0) {
            return ValidationResult.error("Berat dan tinggi harus valid")
        }

        val heightM = heightCm / 100
        val bmi = weightKg / (heightM * heightM)

        return when {
            bmi < 16 -> ValidationResult.warning("BMI ${String.format("%.1f", bmi)}: Sangat kurus (underweight berat)")
            bmi < 18.5 -> ValidationResult.warning("BMI ${String.format("%.1f", bmi)}: Kurus (underweight)")
            bmi < 25 -> ValidationResult.valid() // Normal
            bmi < 30 -> ValidationResult.warning("BMI ${String.format("%.1f", bmi)}: Kelebihan berat badan")
            bmi < 35 -> ValidationResult.warning("BMI ${String.format("%.1f", bmi)}: Obesitas kelas I")
            bmi < 40 -> ValidationResult.warning("BMI ${String.format("%.1f", bmi)}: Obesitas kelas II")
            else -> ValidationResult.warning("BMI ${String.format("%.1f", bmi)}: Obesitas kelas III")
        }
    }

    /**
     * Validate timestamp is not in the future
     */
    fun validateTimestamp(timestamp: Long): ValidationResult {
        val now = System.currentTimeMillis()
        return when {
            timestamp > now + 60000 -> ValidationResult.error("Tanggal tidak boleh di masa depan")
            timestamp < 0 -> ValidationResult.error("Tanggal tidak valid")
            else -> ValidationResult.valid()
        }
    }
}
