package com.example.luminacal.data.ml

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

/**
 * Food detection result with confidence and suggestions
 */
data class FoodDetection(
    val label: String,
    val confidence: Float,
    val category: String,
    val suggestedFoods: List<String>
)

class FoodAnalyzer(
    private val onFoodDetected: (FoodDetection?) -> Unit
) : ImageAnalysis.Analyzer {

    // Indonesian food suggestions by category
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
        )
    )

    // Default suggestions when no specific category matched
    private val defaultSuggestions = listOf(
        "Nasi Putih", "Nasi Goreng", "Mie Goreng", 
        "Ayam Goreng", "Rendang", "Sate Ayam"
    )

    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .enableMultipleObjects()
        .build()

    private val detector = ObjectDetection.getClient(options)
    
    private val confidenceThreshold = 0.5f

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            detector.process(image)
                .addOnSuccessListener { detectedObjects ->
                    // Get all labels with high confidence
                    val bestDetection = detectedObjects
                        .flatMap { obj -> obj.labels }
                        .filter { label -> label.confidence >= confidenceThreshold }
                        .maxByOrNull { it.confidence }
                    
                    if (bestDetection != null) {
                        val category = bestDetection.text
                        val suggestions = foodSuggestions[category] ?: defaultSuggestions
                        
                        // Pick a suggested food based on some variation
                        val suggestedLabel = suggestions.firstOrNull() ?: category
                        
                        val detection = FoodDetection(
                            label = suggestedLabel,
                            confidence = bestDetection.confidence,
                            category = category,
                            suggestedFoods = suggestions
                        )
                        onFoodDetected(detection)
                    } else {
                        onFoodDetected(null)
                    }
                }
                .addOnFailureListener {
                    onFoodDetected(null)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
