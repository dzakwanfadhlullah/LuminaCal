package com.example.luminacal.ui.screens.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.ui.components.CameraPreview
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.data.ml.FoodDetection
import com.example.luminacal.data.ml.FoodNutritionDatabase
import com.example.luminacal.data.ml.NutritionInfo
import com.example.luminacal.ui.theme.Blue500
import com.example.luminacal.ui.theme.Green500
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.luminacal.R
import androidx.core.content.ContextCompat

@Composable
fun CameraScannerScreen(
    onClose: () -> Unit,
    onFoodConfirmed: (NutritionInfo) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var currentDetection by remember { mutableStateOf<FoodDetection?>(null) }
    var selectedFood by remember { mutableStateOf<NutritionInfo?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onFoodDetected = { detection ->
                    currentDetection = detection
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.camera_permission_required),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Camera Overlay UI
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cd_close_button), tint = Color.White)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.FlashOn, contentDescription = stringResource(R.string.cd_flash), tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Focus Area
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.CenterHorizontally)
                    .border(
                        2.dp, 
                        if (currentDetection != null) Green500 else Color.White.copy(alpha = 0.5f), 
                        RoundedCornerShape(32.dp)
                    )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Info Card with detection info
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    if (currentDetection != null) {
                        // Show detected food
                        Text(
                            text = currentDetection!!.label,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.camera_confidence, (currentDetection!!.confidence * 100).toInt()),
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
                                text = currentDetection!!.category,
                                color = Blue500,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        // Nutrition Info (if available)
                        val nutritionInfo = currentDetection!!.nutritionInfo
                        if (nutritionInfo != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${nutritionInfo.calories}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = stringResource(R.string.dashboard_kcal),
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${nutritionInfo.protein}g",
                                        color = Blue500,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = stringResource(R.string.macro_protein).lowercase(),
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${nutritionInfo.carbs}g",
                                        color = Green500,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = stringResource(R.string.macro_carbs).lowercase(),
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${nutritionInfo.fat}g",
                                        color = Color(0xFFFB923C),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = stringResource(R.string.macro_fat).lowercase(),
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                }
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
                    } else {
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
            
            // Suggestion Chips
            if (currentDetection != null) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(currentDetection!!.suggestedFoods) { foodName ->
                        val nutrition = FoodNutritionDatabase.lookup(foodName)
                        val isSelected = selectedFood?.name == foodName
                        
                        Surface(
                            color = if (isSelected) Green500 else Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedFood = nutrition
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Green500
                    ),
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
}
