package com.example.luminacal.ui.screens.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.luminacal.R
import com.example.luminacal.data.ml.FoodDetection
import com.example.luminacal.data.ml.FoodNutritionDatabase
import com.example.luminacal.data.ml.NonFoodDetection
import com.example.luminacal.data.ml.NutritionInfo
import com.example.luminacal.ui.components.CameraError
import com.example.luminacal.ui.components.CameraPreview
import com.example.luminacal.ui.components.CameraState
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.ui.theme.Blue500
import com.example.luminacal.ui.theme.Green500

@Composable
fun CameraScannerScreen(
    onClose: () -> Unit,
    onFoodConfirmed: (NutritionInfo) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    // Camera states
    var currentDetection by remember { mutableStateOf<FoodDetection?>(null) }
    var nonFoodDetection by remember { mutableStateOf<NonFoodDetection?>(null) }
    var selectedFood by remember { mutableStateOf<NutritionInfo?>(null) }
    var cameraState by remember { mutableStateOf<CameraState>(CameraState.Initializing) }
    var cameraError by remember { mutableStateOf<CameraError?>(null) }
    var retryTrigger by remember { mutableIntStateOf(0) }
    
    // Permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDeniedPermanently by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (!granted) {
                permissionDeniedPermanently = true
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview or Error State
        when {
            !hasCameraPermission -> {
                CameraPermissionDeniedUI(
                    isPermanentlyDenied = permissionDeniedPermanently,
                    onRequestPermission = {
                        launcher.launch(Manifest.permission.CAMERA)
                    },
                    onClose = onClose
                )
            }
            cameraError != null -> {
                CameraErrorUI(
                    error = cameraError!!,
                    onRetry = {
                        cameraError = null
                        cameraState = CameraState.Initializing
                        currentDetection = null
                        retryTrigger++
                    },
                    onClose = onClose
                )
            }
            else -> {
                // Key changes when retryTrigger changes to force recomposition
                key(retryTrigger) {
                    CameraPreview(
                        modifier = Modifier.fillMaxSize(),
                        onFoodDetected = { detection ->
                            currentDetection = detection
                            if (detection != null) {
                                nonFoodDetection = null // Clear non-food when food detected
                            }
                        },
                        onNonFoodDetected = { nonFood ->
                            nonFoodDetection = nonFood
                            if (nonFood != null) {
                                currentDetection = null // Clear food when non-food detected
                                selectedFood = null
                            }
                        },
                        onCameraStateChanged = { state ->
                            cameraState = state
                            if (state is CameraState.Error) {
                                cameraError = state.error
                            }
                        },
                        onError = { error ->
                            cameraError = error
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
                }
            }
        }

        // Camera Overlay UI (only show when camera is active)
        AnimatedVisibility(
            visible = hasCameraPermission && cameraError == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            CameraOverlayUI(
                cameraState = cameraState,
                currentDetection = currentDetection,
                nonFoodDetection = nonFoodDetection,
                selectedFood = selectedFood,
                onSelectedFoodChange = { selectedFood = it },
                onFoodConfirmed = onFoodConfirmed,
                onClose = onClose
            )
        }
    }
}

/**
 * UI shown when camera permission is denied
 */
@Composable
private fun CameraPermissionDeniedUI(
    isPermanentlyDenied: Boolean,
    onRequestPermission: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(R.string.camera_permission_required),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = if (isPermanentlyDenied) {
                    stringResource(R.string.camera_permission_settings_hint)
                } else {
                    stringResource(R.string.camera_permission_hint)
                },
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (!isPermanentlyDenied) {
                Button(
                    onClick = onRequestPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = Green500),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.grant_permission))
                }
                
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            TextButton(onClick = onClose) {
                Text(
                    stringResource(R.string.go_back),
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * UI shown when camera encounters an error
 */
@Composable
private fun CameraErrorUI(
    error: CameraError,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val (icon, title, description) = remember(error) {
        when (error) {
            is CameraError.ProviderUnavailable -> Triple(
                Icons.Default.ErrorOutline,
                "Camera Unavailable",
                "Could not access the camera. Please try again or restart the app."
            )
            is CameraError.BindingFailed -> Triple(
                Icons.Default.SyncProblem,
                "Camera Binding Failed",
                "Failed to initialize camera preview. This may be a temporary issue."
            )
            is CameraError.NoCameraAvailable -> Triple(
                Icons.Default.NoPhotography,
                "No Camera Found",
                "This device does not have a compatible camera for food scanning."
            )
            is CameraError.AnalyzerFailed -> Triple(
                Icons.Default.Psychology,
                "AI Scanner Error",
                "The food recognition system encountered an error. Please try again."
            )
            is CameraError.Unknown -> Triple(
                Icons.Default.Warning,
                "Unexpected Error",
                error.message
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1E)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Error icon with pulsing animation
            val infiniteTransition = rememberInfiniteTransition(label = "error_pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "error_alpha"
            )
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFFF453A).copy(alpha = alpha),
                modifier = Modifier.size(72.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Retry button
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Green500),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(R.string.try_again),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Close button
            OutlinedButton(
                onClick = onClose,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White.copy(alpha = 0.8f)
                ),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.3f))
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    stringResource(R.string.go_back),
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

/**
 * Main camera overlay with detection info and controls
 */
@Composable
private fun CameraOverlayUI(
    cameraState: CameraState,
    currentDetection: FoodDetection?,
    nonFoodDetection: NonFoodDetection?,
    selectedFood: NutritionInfo?,
    onSelectedFoodChange: (NutritionInfo?) -> Unit,
    onFoodConfirmed: (NutritionInfo) -> Unit,
    onClose: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_close_button),
                    tint = Color.White
                )
            }
            
            // Camera state indicator
            CameraStateIndicator(state = cameraState)
            
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.FlashOn,
                    contentDescription = stringResource(R.string.cd_flash),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Focus Area with animated border
        FocusAreaBox(
            hasDetection = currentDetection != null,
            isNonFood = nonFoodDetection != null
        )

        Spacer(modifier = Modifier.weight(1f))

        // Info Card with detection info
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                when {
                    cameraState is CameraState.Initializing -> {
                        LoadingIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.camera_initializing),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                    nonFoodDetection != null -> {
                        // Show "Not a food item" message
                        NotFoodInfoCard(nonFoodDetection = nonFoodDetection)
                    }
                    currentDetection != null -> {
                        DetectionInfoCard(detection = currentDetection)
                    }
                    else -> {
                        // Scanning state
                        Text(
                            text = stringResource(R.string.scanning),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = stringResource(R.string.scanning_hint),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Suggestion Chips
        if (currentDetection != null) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(currentDetection.suggestedFoods) { foodName ->
                    val nutrition = FoodNutritionDatabase.lookup(foodName)
                    val isSelected = selectedFood?.name == foodName

                    Surface(
                        color = if (isSelected) Green500 else Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelectedFoodChange(nutrition)
                        }
                    ) {
                        Text(
                            text = if (nutrition != null) "$foodName (${nutrition.calories})" else foodName,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Confirm Button
        val foodToLog = selectedFood ?: currentDetection?.nutritionInfo
        if (foodToLog != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onFoodConfirmed(foodToLog)
                    onClose()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green500),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.log_food_action, foodToLog.name, foodToLog.calories),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Camera state indicator with icon
 */
@Composable
private fun CameraStateIndicator(state: CameraState) {
    val (icon, color, label) = when (state) {
        is CameraState.Initializing -> Triple(Icons.Default.HourglassEmpty, Color.Yellow, "Starting...")
        is CameraState.Ready -> Triple(Icons.Default.CameraAlt, Color.White, "Ready")
        is CameraState.Analyzing -> Triple(Icons.Default.AutoAwesome, Green500, "Detecting")
        is CameraState.Error -> Triple(Icons.Default.ErrorOutline, Color.Red, "Error")
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Spinning animation for initializing state
        val rotation = if (state is CameraState.Initializing) {
            val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
            infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing)
                ),
                label = "rotation"
            ).value
        } else 0f
        
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .size(16.dp)
                .rotate(rotation)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Animated focus area box
 */
@Composable
private fun FocusAreaBox(hasDetection: Boolean, isNonFood: Boolean = false) {
    val borderColor = when {
        isNonFood -> Color(0xFFFF453A) // Red for non-food
        hasDetection -> Green500
        else -> Color.White.copy(alpha = 0.5f)
    }
    val animatedBorderWidth by animateDpAsState(
        targetValue = when {
            isNonFood -> 3.dp
            hasDetection -> 3.dp
            else -> 2.dp
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "border_width"
    )
    
    Box(
        modifier = Modifier
            .size(280.dp)
            .border(animatedBorderWidth, borderColor, RoundedCornerShape(32.dp))
    )
}

/**
 * Loading indicator for camera initialization
 */
@Composable
private fun LoadingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "loading_rotation"
    )
    
    CircularProgressIndicator(
        color = Green500,
        strokeWidth = 2.dp,
        modifier = Modifier
            .size(24.dp)
            .rotate(rotation)
    )
}

/**
 * Detection info display card
 */
@Composable
private fun DetectionInfoCard(detection: FoodDetection) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = detection.label,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.camera_confidence, (detection.confidence * 100).toInt()),
                color = Green500,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.ai_detected),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
            Text(
                text = detection.category,
                color = Blue500,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Nutrition Info (if available)
        val nutritionInfo = detection.nutritionInfo
        if (nutritionInfo != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionColumn("${nutritionInfo.calories}", stringResource(R.string.dashboard_kcal), Color.White)
                NutritionColumn("${nutritionInfo.protein}g", stringResource(R.string.macro_protein).lowercase(), Blue500)
                NutritionColumn("${nutritionInfo.carbs}g", stringResource(R.string.macro_carbs).lowercase(), Green500)
                NutritionColumn("${nutritionInfo.fat}g", stringResource(R.string.macro_fat).lowercase(), Color(0xFFFB923C))
            }
            Text(
                text = nutritionInfo.servingSize,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.ai_suggestion_hint),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun NutritionColumn(value: String, label: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            fontSize = if (valueColor == Color.White) 18.sp else 16.sp
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}

/**
 * Info card shown when a non-food item is detected
 */
@Composable
private fun NotFoodInfoCard(nonFoodDetection: NonFoodDetection) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.NotInterested,
            contentDescription = null,
            tint = Color(0xFFFF453A),
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.not_a_food_item),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = stringResource(R.string.detected_as, nonFoodDetection.detectedCategory),
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.point_at_food_hint),
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

