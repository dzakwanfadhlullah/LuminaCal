package com.example.luminacal.data.ml

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

private const val TAG = "FoodAnalyzer"

/**
 * Food detection result with confidence, suggestions, and nutrition info
 */
data class FoodDetection(
    val label: String,
    val confidence: Float,
    val category: String,
    val suggestedFoods: List<String>,
    val nutritionInfo: NutritionInfo? = null,
    val isValidFood: Boolean = true
)

/**
 * Non-food detection result to show "Not a food item" message
 */
data class NonFoodDetection(
    val detectedCategory: String,
    val confidence: Float
)

/**
 * Enhanced FoodAnalyzer with:
 * 1. Higher confidence threshold (0.7f)
 * 2. Non-food category filtering
 * 3. Detection debounce to prevent rapid changes
 * 4. Proper food validation before showing results
 */
class FoodAnalyzer(
    private val onFoodDetected: (FoodDetection?) -> Unit,
    private val onNonFoodDetected: (NonFoodDetection?) -> Unit = {}
) : ImageAnalysis.Analyzer {

    // ==================== CONFIGURATION ====================
    
    /**
     * Higher confidence threshold to reduce false positives
     * Changed from 0.5f to 0.7f
     */
    private val confidenceThreshold = 0.7f
    
    /**
     * Minimum confidence to even consider a detection
     */
    private val minimumDetectionThreshold = 0.5f
    
    /**
     * Debounce: minimum time between detection updates (ms)
     * Prevents rapid flickering of suggestions
     */
    private val debounceIntervalMs = 500L
    private var lastDetectionTime = 0L
    private var lastDetectedCategory: String? = null
    
    /**
     * Counter for stable detection - require same category multiple times
     */
    private var stableDetectionCount = 0
    private val requiredStableCount = 3 // Need 3 consecutive same detections
    
    // ==================== CATEGORY DEFINITIONS ====================
    
    /**
     * Categories that are DEFINITELY food-related
     * ML Kit Object Detection categories
     */
    private val validFoodCategories = setOf(
        "Food",
        "Drink", 
        "Fruit",
        "Vegetable",
        "Plant", // vegetables often detected as Plant
        "Snack",
        "Dessert",
        "Baked goods",
        "Dairy",
        "Seafood",
        "Meat"
    )
    
    /**
     * Categories that are DEFINITELY NOT food
     * These will trigger "Not a food item" message
     */
    private val nonFoodCategories = setOf(
        "Person",
        "Human face",
        "Face",
        "Human body",
        "Human hand",
        "Human arm",
        "Human leg",
        "Clothing",
        "Fashion accessory",
        "Footwear",
        "Glasses",
        "Hat",
        "Helmet",
        "Watch",
        "Animal",
        "Pet",
        "Dog",
        "Cat",
        "Bird",
        "Car",
        "Vehicle",
        "Bicycle",
        "Motorcycle",
        "Electronics",
        "Phone",
        "Computer",
        "Laptop",
        "Tablet",
        "Television",
        "Camera",
        "Book",
        "Furniture",
        "Chair",
        "Table",
        "Couch",
        "Bed",
        "Building",
        "House",
        "Office",
        "Sports equipment",
        "Ball",
        "Musical instrument",
        "Tool",
        "Toy",
        "Weapon",
        "Home goods",
        "Kitchen appliance",
        "Packaged goods" // Usually product packaging, not actual food
    )

    // ==================== FOOD SUGGESTIONS ====================
    
    /**
     * Indonesian food suggestions by category
     */
    private val foodSuggestions = mapOf(
        "Food" to listOf(
            "Nasi Goreng", "Rendang", "Sate Ayam", "Gado-Gado", 
            "Bakso", "Mie Goreng", "Nasi Uduk", "Ayam Geprek",
            "Soto Ayam", "Opor Ayam", "Ikan Bakar", "Tempe Goreng"
        ),
        "Drink" to listOf(
            "Es Teh", "Es Jeruk", "Kopi", "Jus Alpukat",
            "Es Campur", "Cendol", "Teh Tarik", "Susu"
        ),
        "Fruit" to listOf(
            "Pisang", "Mangga", "Jeruk", "Apel",
            "Semangka", "Pepaya", "Nanas", "Durian"
        ),
        "Plant" to listOf(
            "Sayur Asem", "Sayur Lodeh", "Capcay", "Tumis Kangkung",
            "Pecel", "Urap", "Lalapan"
        ),
        "Vegetable" to listOf(
            "Sayur Asem", "Sayur Lodeh", "Capcay", "Tumis Kangkung",
            "Pecel", "Urap", "Lalapan", "Gado-Gado"
        ),
        "Snack" to listOf(
            "Gorengan", "Pisang Goreng", "Risol", "Martabak Manis",
            "Pempek", "Tahu Isi", "Tempe Mendoan"
        ),
        "Baked goods" to listOf(
            "Martabak Manis", "Pisang Goreng", "Gorengan"
        ),
        "Seafood" to listOf(
            "Ikan Bakar", "Ikan Goreng", "Pempek"
        ),
        "Meat" to listOf(
            "Rendang", "Sate Ayam", "Ayam Goreng", "Ayam Bakar",
            "Bebek Goreng", "Opor Ayam"
        )
    )

    /**
     * Default suggestions when category matched but no specific suggestions
     */
    private val defaultFoodSuggestions = listOf(
        "Nasi Putih", "Nasi Goreng", "Mie Goreng", 
        "Ayam Goreng", "Rendang", "Sate Ayam"
    )

    // ==================== ML KIT DETECTOR ====================
    
    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .enableMultipleObjects()
        .build()

    private val detector = ObjectDetection.getClient(options)

    // ==================== ANALYSIS ====================
    
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        
        val currentTime = System.currentTimeMillis()
        
        // Debounce check - skip if too soon after last detection
        if (currentTime - lastDetectionTime < debounceIntervalMs) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        
        detector.process(image)
            .addOnSuccessListener { detectedObjects ->
                processDetections(detectedObjects, currentTime)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Detection failed: ${e.message}")
                resetDetection()
                onFoodDetected(null)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
    
    /**
     * Process detected objects with filtering and validation
     */
    private fun processDetections(
        detectedObjects: List<com.google.mlkit.vision.objects.DetectedObject>,
        currentTime: Long
    ) {
        // Get all labels from all detected objects
        val allLabels = detectedObjects
            .flatMap { obj -> obj.labels }
            .filter { label -> label.confidence >= minimumDetectionThreshold }
            .sortedByDescending { it.confidence }
        
        if (allLabels.isEmpty()) {
            resetDetection()
            onFoodDetected(null)
            onNonFoodDetected(null)
            return
        }
        
        // Find the best detection
        val bestLabel = allLabels.first()
        val category = bestLabel.text
        val confidence = bestLabel.confidence
        
        Log.d(TAG, "Detected: $category with confidence $confidence")
        
        // Check if it's a non-food item
        if (isNonFoodCategory(category)) {
            Log.d(TAG, "Non-food detected: $category")
            resetDetection()
            onFoodDetected(null)
            onNonFoodDetected(NonFoodDetection(category, confidence))
            return
        }
        
        // Check if it's a valid food category with high confidence
        if (isValidFoodCategory(category) && confidence >= confidenceThreshold) {
            // Stable detection check
            if (category == lastDetectedCategory) {
                stableDetectionCount++
            } else {
                stableDetectionCount = 1
                lastDetectedCategory = category
            }
            
            // Only emit if we have stable detection
            if (stableDetectionCount >= requiredStableCount) {
                lastDetectionTime = currentTime
                
                val suggestions = foodSuggestions[category] ?: defaultFoodSuggestions
                val suggestedLabel = suggestions.firstOrNull() ?: category
                val nutritionInfo = FoodNutritionDatabase.lookup(suggestedLabel)
                
                val detection = FoodDetection(
                    label = suggestedLabel,
                    confidence = confidence,
                    category = category,
                    suggestedFoods = suggestions,
                    nutritionInfo = nutritionInfo,
                    isValidFood = true
                )
                
                Log.d(TAG, "Valid food detected: $suggestedLabel (stable count: $stableDetectionCount)")
                onFoodDetected(detection)
                onNonFoodDetected(null)
            }
        } else {
            // Unknown category or low confidence - show nothing
            Log.d(TAG, "Category not recognized as food or low confidence: $category ($confidence)")
            resetDetection()
            onFoodDetected(null)
            onNonFoodDetected(null)
        }
    }
    
    /**
     * Check if category is a valid food category
     */
    private fun isValidFoodCategory(category: String): Boolean {
        return validFoodCategories.any { 
            category.equals(it, ignoreCase = true) 
        }
    }
    
    /**
     * Check if category is definitely NOT food
     */
    private fun isNonFoodCategory(category: String): Boolean {
        return nonFoodCategories.any { 
            category.contains(it, ignoreCase = true) || 
            it.contains(category, ignoreCase = true)
        }
    }
    
    /**
     * Reset detection state
     */
    private fun resetDetection() {
        stableDetectionCount = 0
        lastDetectedCategory = null
    }
}
