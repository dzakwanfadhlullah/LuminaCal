package com.example.luminacal.ui.screens.health

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.data.repository.WeightEntry
import com.example.luminacal.data.repository.WeightTrend
import com.example.luminacal.model.*
import com.example.luminacal.ui.components.AddWeightDialog
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.ui.components.WeightEntryCard
import com.example.luminacal.ui.components.WeightTrendBadge
import com.example.luminacal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMetricsScreen(
    onBack: () -> Unit,
    onApplyGoals: (HealthMetrics) -> Unit,
    weightHistory: List<WeightEntry> = emptyList(),
    weightTrend: WeightTrend = WeightTrend(null, null, null),
    onAddWeight: (Float, String?) -> Unit = { _, _ -> },
    onDeleteWeight: (WeightEntry) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    
    // State for health metrics
    var weight by remember { mutableFloatStateOf(70f) }
    var height by remember { mutableFloatStateOf(170f) }
    var age by remember { mutableIntStateOf(25) }
    var gender by remember { mutableStateOf(Gender.MALE) }
    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATE) }
    var fitnessGoal by remember { mutableStateOf(FitnessGoal.MAINTAIN) }
    
    // Dialog state
    var showAddWeightDialog by remember { mutableStateOf(false) }
    
    // Create health metrics object
    val healthMetrics = remember(weight, height, age, gender, activityLevel, fitnessGoal) {
        HealthMetrics(weight, height, age, gender, activityLevel, fitnessGoal)
    }
    
    // Animated TDEE value
    val animatedTdee by animateIntAsState(
        targetValue = healthMetrics.targetCalories,
        animationSpec = tween(500),
        label = "tdee"
    )
    
    // Show add weight dialog
    if (showAddWeightDialog) {
        AddWeightDialog(
            currentWeight = weight,
            onDismiss = { showAddWeightDialog = false },
            onConfirm = { newWeight, note ->
                onAddWeight(newWeight, note)
                showAddWeightDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onBack()
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Health & Goals",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // TDEE Display Card
        item {
            TdeeDisplayCard(
                tdee = animatedTdee,
                bmr = healthMetrics.bmr,
                protein = healthMetrics.recommendedProtein,
                carbs = healthMetrics.recommendedCarbs,
                fat = healthMetrics.recommendedFat
            )
        }

        // Weight History Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weight History",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WeightTrendBadge(trend = weightTrend)
                    FilledTonalButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showAddWeightDialog = true
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Weight Entries
        if (weightHistory.isNotEmpty()) {
            items(weightHistory.take(5)) { entry ->
                WeightEntryCard(
                    entry = entry,
                    onDelete = { onDeleteWeight(entry) }
                )
            }
        } else {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No weight entries yet. Tap 'Log' to add your first entry.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Body Metrics Section
        item {
            Text(
                text = "Body Metrics",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        // Weight Slider
        item {
            MetricSliderCard(
                label = "Weight",
                value = weight,
                unit = "kg",
                range = 40f..200f,
                icon = Icons.Default.FitnessCenter,
                onValueChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    weight = it
                }
            )
        }

        // Height Slider
        item {
            MetricSliderCard(
                label = "Height",
                value = height,
                unit = "cm",
                range = 120f..220f,
                icon = Icons.Default.Height,
                onValueChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    height = it
                }
            )
        }

        // Age Slider
        item {
            MetricSliderCard(
                label = "Age",
                value = age.toFloat(),
                unit = "years",
                range = 15f..80f,
                icon = Icons.Default.Cake,
                onValueChange = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    age = it.toInt()
                }
            )
        }

        // Gender Selection
        item {
            Text(
                text = "Gender",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        item {
            GenderSelector(
                selected = gender,
                onSelect = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    gender = it
                }
            )
        }

        // Activity Level
        item {
            Text(
                text = "Activity Level",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        item {
            ActivityLevelSelector(
                selected = activityLevel,
                onSelect = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    activityLevel = it
                }
            )
        }

        // Fitness Goal
        item {
            Text(
                text = "Fitness Goal",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        item {
            FitnessGoalSelector(
                selected = fitnessGoal,
                onSelect = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    fitnessGoal = it
                }
            )
        }

        // Apply Button
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onApplyGoals(healthMetrics)
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue500
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apply Goals", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun TdeeDisplayCard(
    tdee: Int,
    bmr: Int,
    protein: Int,
    carbs: Int,
    fat: Int
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Daily Calorie Target",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$tdee",
                style = MaterialTheme.typography.displayLarge,
                color = Peach400,
                fontWeight = FontWeight.Black
            )
            
            Text(
                text = "kcal / day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroInfo("BMR", "$bmr", Color.Gray)
                MacroInfo("Protein", "${protein}g", Blue500)
                MacroInfo("Carbs", "${carbs}g", Green500)
                MacroInfo("Fat", "${fat}g", Peach400)
            }
        }
    }
}

@Composable
fun MacroInfo(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = color,
            fontSize = 14.sp
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun MetricSliderCard(
    label: String,
    value: Float,
    unit: String,
    range: ClosedFloatingPointRange<Float>,
    icon: ImageVector,
    onValueChange: (Float) -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Blue500.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Blue500)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${value.toInt()} $unit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Blue500,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Slider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = range,
                    colors = SliderDefaults.colors(
                        thumbColor = Blue500,
                        activeTrackColor = Blue500,
                        inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}

@Composable
fun GenderSelector(
    selected: Gender,
    onSelect: (Gender) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Gender.entries.forEach { gender ->
            val isSelected = gender == selected
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "genderScale"
            )
            
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .scale(scale)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) Blue500 else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ),
                onClick = { onSelect(gender) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = when (gender) {
                            Gender.MALE -> Icons.Default.Male
                            Gender.FEMALE -> Icons.Default.Female
                            Gender.OTHER -> Icons.Default.Transgender
                        },
                        contentDescription = null,
                        tint = if (isSelected) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = gender.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityLevelSelector(
    selected: ActivityLevel,
    onSelect: (ActivityLevel) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ActivityLevel.entries) { level ->
            val isSelected = level == selected
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "activityScale"
            )
            
            GlassCard(
                modifier = Modifier
                    .width(100.dp)
                    .scale(scale)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) Green500 else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    ),
                onClick = { onSelect(level) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        text = when (level) {
                            ActivityLevel.SEDENTARY -> "🛋️"
                            ActivityLevel.LIGHT -> "🚶"
                            ActivityLevel.MODERATE -> "🏃"
                            ActivityLevel.ACTIVE -> "🏋️"
                            ActivityLevel.EXTRA_ACTIVE -> "🔥"
                        },
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = level.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Green500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1
                    )
                    Text(
                        text = "×${level.multiplier}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
fun FitnessGoalSelector(
    selected: FitnessGoal,
    onSelect: (FitnessGoal) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FitnessGoal.entries.forEach { goal ->
            val isSelected = goal == selected
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "goalScale"
            )
            
            val gradientColors = when (goal) {
                FitnessGoal.LOSE_WEIGHT -> listOf(Color(0xFFEF4444), Color(0xFFF97316))
                FitnessGoal.MAINTAIN -> listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
                FitnessGoal.GAIN_MUSCLE -> listOf(Color(0xFF22C55E), Color(0xFF10B981))
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isSelected) Brush.horizontalGradient(gradientColors)
                        else Brush.horizontalGradient(listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f)))
                    )
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(goal) }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (goal) {
                            FitnessGoal.LOSE_WEIGHT -> "📉"
                            FitnessGoal.MAINTAIN -> "⚖️"
                            FitnessGoal.GAIN_MUSCLE -> "💪"
                        },
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = goal.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${if (goal.calorieAdjustment >= 0) "+" else ""}${goal.calorieAdjustment}",
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
