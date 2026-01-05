package com.example.luminacal.ui.components

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.luminacal.data.ml.FoodAnalyzer
import com.example.luminacal.data.ml.FoodDetection
import com.example.luminacal.data.ml.NonFoodDetection
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Camera error types for granular error handling
 */
sealed class CameraError {
    data object ProviderUnavailable : CameraError()
    data object BindingFailed : CameraError()
    data object NoCameraAvailable : CameraError()
    data object AnalyzerFailed : CameraError()
    data class Unknown(val message: String) : CameraError()
}

/**
 * Camera state for UI feedback
 */
sealed class CameraState {
    data object Initializing : CameraState()
    data object Ready : CameraState()
    data object Analyzing : CameraState()
    data class Error(val error: CameraError) : CameraState()
}

private const val TAG = "CameraPreview"
private const val EXECUTOR_SHUTDOWN_TIMEOUT_MS = 500L

/**
 * Robust CameraPreview composable with proper error handling,
 * lifecycle management, and resource cleanup.
 * 
 * Key improvements:
 * 1. DisposableEffect for proper lifecycle management
 * 2. Comprehensive try-catch with categorized errors
 * 3. ExecutorService cleanup to prevent memory leaks
 * 4. Camera state callbacks for UI feedback
 * 5. Graceful fallback on camera binding failure
 */
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    onFoodDetected: (FoodDetection?) -> Unit = {},
    onNonFoodDetected: (NonFoodDetection?) -> Unit = {},
    onCameraStateChanged: (CameraState) -> Unit = {},
    onError: (CameraError) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    
    // Track camera provider future and executor for cleanup
    var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? by remember { mutableStateOf(null) }
    var analysisExecutor: ExecutorService? by remember { mutableStateOf(null) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    
    // Notify initializing state
    LaunchedEffect(Unit) {
        onCameraStateChanged(CameraState.Initializing)
    }
    
    // Cleanup on disposal - CRITICAL for preventing crashes and leaks
    DisposableEffect(lifecycleOwner) {
        onDispose {
            Log.d(TAG, "Disposing camera resources")
            
            // Unbind all camera use cases
            try {
                cameraProvider?.unbindAll()
                Log.d(TAG, "Camera use cases unbound")
            } catch (e: Exception) {
                Log.w(TAG, "Error unbinding camera: ${e.message}")
            }
            
            // Shutdown executor gracefully
            analysisExecutor?.let { executor ->
                try {
                    executor.shutdown()
                    if (!executor.awaitTermination(EXECUTOR_SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                        executor.shutdownNow()
                        Log.w(TAG, "Executor did not terminate gracefully, forced shutdown")
                    } else {
                        Log.d(TAG, "Executor shutdown successfully")
                    }
                } catch (e: InterruptedException) {
                    executor.shutdownNow()
                    Thread.currentThread().interrupt()
                    Log.w(TAG, "Executor shutdown interrupted: ${e.message}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error shutting down executor: ${e.message}")
                }
            }
            
            analysisExecutor = null
            cameraProvider = null
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Enable hardware acceleration for better performance
                setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            }

            try {
                // Get camera provider instance
                cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                
                cameraProviderFuture?.addListener({
                    try {
                        val provider = cameraProviderFuture?.get()
                        
                        if (provider == null) {
                            Log.e(TAG, "Camera provider is null")
                            val error = CameraError.ProviderUnavailable
                            onError(error)
                            onCameraStateChanged(CameraState.Error(error))
                            return@addListener
                        }
                        
                        cameraProvider = provider
                        
                        // Check if camera is available
                        val hasBackCamera = provider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
                        if (!hasBackCamera) {
                            Log.e(TAG, "No back camera available")
                            val error = CameraError.NoCameraAvailable
                            onError(error)
                            onCameraStateChanged(CameraState.Error(error))
                            return@addListener
                        }

                        // Build preview use case
                        val preview = Preview.Builder()
                            .build()
                            .also { p ->
                                p.setSurfaceProvider(previewView.surfaceProvider)
                            }

                        // Create analysis executor
                        val executor = Executors.newSingleThreadExecutor { runnable ->
                            Thread(runnable, "CameraAnalysisThread").apply {
                                // Set lower priority to prevent blocking UI
                                priority = Thread.MIN_PRIORITY + 1
                            }
                        }
                        analysisExecutor = executor

                        // Build image analysis use case with error handling
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                            .build()
                            .also { analysis ->
                                try {
                                    analysis.setAnalyzer(
                                        executor,
                                        FoodAnalyzer(
                                            onFoodDetected = { detection ->
                                                try {
                                                    onFoodDetected(detection)
                                                    if (detection != null) {
                                                        onCameraStateChanged(CameraState.Analyzing)
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Error in food detection callback: ${e.message}")
                                                }
                                            },
                                            onNonFoodDetected = { nonFood ->
                                                try {
                                                    onNonFoodDetected(nonFood)
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Error in non-food detection callback: ${e.message}")
                                                }
                                            }
                                        )
                                    )
                                } catch (e: Exception) {
                                    Log.e(TAG, "Failed to set analyzer: ${e.message}")
                                    val error = CameraError.AnalyzerFailed
                                    onError(error)
                                }
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        // Unbind all previous use cases before binding new ones
                        provider.unbindAll()

                        // Bind use cases to lifecycle
                        provider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                        
                        Log.d(TAG, "Camera bound successfully")
                        onCameraStateChanged(CameraState.Ready)
                        
                    } catch (e: java.util.concurrent.ExecutionException) {
                        Log.e(TAG, "Camera provider future execution failed", e)
                        val error = CameraError.ProviderUnavailable
                        onError(error)
                        onCameraStateChanged(CameraState.Error(error))
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "Camera binding failed - lifecycle issue", e)
                        val error = CameraError.BindingFailed
                        onError(error)
                        onCameraStateChanged(CameraState.Error(error))
                    } catch (e: IllegalArgumentException) {
                        Log.e(TAG, "Camera binding failed - invalid configuration", e)
                        val error = CameraError.BindingFailed
                        onError(error)
                        onCameraStateChanged(CameraState.Error(error))
                    } catch (e: Exception) {
                        Log.e(TAG, "Unexpected camera error", e)
                        val error = CameraError.Unknown(e.message ?: "Unknown error")
                        onError(error)
                        onCameraStateChanged(CameraState.Error(error))
                    }
                }, ContextCompat.getMainExecutor(ctx))
                
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize camera provider", e)
                val error = CameraError.ProviderUnavailable
                onError(error)
                onCameraStateChanged(CameraState.Error(error))
            }

            previewView
        },
        update = { _ ->
            // No-op: Camera lifecycle is managed by DisposableEffect
        }
    )
}
