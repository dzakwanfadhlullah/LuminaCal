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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.R
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
    savedHealthMetrics: HealthMetrics = HealthMetrics(),
    onBack: () -> Unit,
    onApplyGoals: (HealthMetrics) -> Unit,
    weightHistory: List<WeightEntry> = emptyList(),
    weightTrend: WeightTrend = WeightTrend(null, null, null),
    onAddWeight: (Float, String?) -> Unit = { _, _ -> },
    onDeleteWeight: (WeightEntry) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    
    // State for health metrics - initialized from saved values
    var weight by remember { mutableFloatStateOf(savedHealthMetrics.weight) }
    var targetWeight by remember { mutableFloatStateOf(savedHealthMetrics.targetWeight) }
    var height by remember { mutableFloatStateOf(savedHealthMetrics.height) }
    var age by remember { mutableIntStateOf(savedHealthMetrics.age) }
    var gender by remember { mutableStateOf(savedHealthMetrics.gender) }
    var activityLevel by remember { mutableStateOf(savedHealthMetrics.activityLevel) }
    var fitnessGoal by remember { mutableStateOf(savedHealthMetrics.fitnessGoal) }
    
    // Dialog state
    var showAddWeightDialog by remember { mutableStateOf(false) }
    
    // Create health metrics object
    val healthMetrics = remember(weight, targetWeight, height, age, gender, activityLevel, fitnessGoal) {
        HealthMetrics(
            userName = savedHealthMetrics.userName,
            weight = weight,
            targetWeight = targetWeight,
            height = height,
            age = age,
            gender = gender,
            activityLevel = activityLevel,
            fitnessGoal = fitnessGoal,
            waterTargetMl = savedHealthMetrics.waterTargetMl
        )
    }
    
    // BMI Validation
    val bmiValidation = remember(weight, height) {
        com.example.luminacal.util.ValidationUtils.validateBMI(weight, height)
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
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = stringResource(R.string.health_metrics_title),
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
        
        // BMI Display Card
        item {
            BmiDisplayCard(
                bmi = healthMetrics.bmi,
                category = healthMetrics.bmiCategory
            )
        }
        
        // Estimated Time to Goal Card
        item {
            TimeToGoalCard(
                currentWeight = healthMetrics.weight,
                targetWeight = healthMetrics.targetWeight,
                weeksToGoal = healthMetrics.estimatedWeeksToGoal,
                fitnessGoal = healthMetrics.fitnessGoal
            )
        }
        
        // BMI Warning Banner (if applicable)
        bmiValidation.warningMessage?.let { warning ->
            item {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = warning,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }
        }

        // Weight History Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.weight_history),
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
                        Text(stringResource(R.string.log_weight), style = MaterialTheme.typography.labelMedium)
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
                        text = stringResource(R.string.no_weight_data),
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
                text = stringResource(R.string.body_metrics_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        // Weight Slider
        item {
            MetricSliderCard(
                label = stringResource(R.string.weight_title),
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
                label = stringResource(R.string.height_title),
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
                label = stringResource(R.string.age_title),
                value = age.toFloat(),
                unit = stringResource(R.string.age_years, 0).replace("0 ", ""), // Just get "years" or similar
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
                text = stringResource(R.string.activity_level_title),
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
                text = stringResource(R.string.fitness_goal_title),
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
                Text(stringResource(R.string.update_goals), fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                text = stringResource(R.string.tdee_title),
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
                text = stringResource(R.string.calories_per_day),
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
                MacroInfo(stringResource(R.string.bmr_label), "$bmr", Color.Gray)
                MacroInfo(stringResource(R.string.macro_protein), "${protein}g", Blue500)
                MacroInfo(stringResource(R.string.macro_carbs), "${carbs}g", Green500)
                MacroInfo(stringResource(R.string.macro_fat), "${fat}g", Peach400)
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
fun BmiDisplayCard(
    bmi: Float,
    category: com.example.luminacal.model.BmiCategory
) {
    val categoryColor = Color(category.colorHex)
    
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Body Mass Index",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    color = categoryColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = category.label,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = categoryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // BMI Value Display
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = String.format("%.1f", bmi),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = categoryColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "kg/m²",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // BMI Scale Visual
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                Box(modifier = Modifier.weight(18.5f).fillMaxHeight().background(Color(0xFF3B82F6))) // Underweight
                Box(modifier = Modifier.weight(6.5f).fillMaxHeight().background(Color(0xFF22C55E)))  // Normal
                Box(modifier = Modifier.weight(5f).fillMaxHeight().background(Color(0xFFF59E0B)))    // Overweight
                Box(modifier = Modifier.weight(10f).fillMaxHeight().background(Color(0xFFEF4444)))   // Obese
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("< 18.5", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                Text("18.5-25", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                Text("25-30", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                Text("> 30", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
fun TimeToGoalCard(
    currentWeight: Float,
    targetWeight: Float,
    weeksToGoal: Int?,
    fitnessGoal: com.example.luminacal.model.FitnessGoal
) {
    val weightDiff = targetWeight - currentWeight
    val isLosing = weightDiff < 0
    val diffAbs = kotlin.math.abs(weightDiff)
    
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Goal Timeline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    tint = Peach400,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Weight journey visualization
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${currentWeight.toInt()}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Current",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (isLosing) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = if (isLosing) Green500 else Blue500,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "${if (isLosing) "-" else "+"}${String.format("%.1f", diffAbs)} kg",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isLosing) Green500 else Blue500
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${targetWeight.toInt()}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Peach400
                    )
                    Text(
                        text = "Target",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timeline estimate
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Peach400.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Peach400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    when {
                        weeksToGoal == 0 -> {
                            Text(
                                text = "🎉 You've reached your goal!",
                                fontWeight = FontWeight.Medium,
                                color = Green500
                            )
                        }
                        weeksToGoal == null -> {
                            Text(
                                text = "Set a weight loss/gain goal to see timeline",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        else -> {
                            Text(
                                text = "Estimated: ~$weeksToGoal weeks (${weeksToGoal / 4} months)",
                                fontWeight = FontWeight.Medium,
                                color = Peach400
                            )
                        }
                    }
                }
            }
        }
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
                        text = when (gender) {
                            Gender.MALE -> stringResource(R.string.gender_male)
                            Gender.FEMALE -> stringResource(R.string.gender_female)
                            Gender.OTHER -> stringResource(R.string.gender_other)
                        },
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
                        text = stringResource(level.labelResId),
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
                        text = stringResource(goal.labelResId),
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

/**
 * Weight statistics card showing weekly/monthly averages and min/max
 */
@Composable
fun WeightStatsCard(
    stats: com.example.luminacal.data.repository.WeightStats
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Weight Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Weekly Average
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stats.weeklyAverage?.let { String.format("%.1f", it) } ?: "--",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Blue500
                    )
                    Text(
                        text = "Weekly Avg",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                
                // Monthly Average
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stats.monthlyAverage?.let { String.format("%.1f", it) } ?: "--",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Green500
                    )
                    Text(
                        text = "Monthly Avg",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Min/Max Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = Green500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stats.minWeight?.let { String.format("%.1f kg", it) } ?: "--",
                            fontWeight = FontWeight.Bold,
                            color = Green500
                        )
                    }
                    Text(
                        text = "Lowest",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = Pink500,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stats.maxWeight?.let { String.format("%.1f kg", it) } ?: "--",
                            fontWeight = FontWeight.Bold,
                            color = Pink500
                        )
                    }
                    Text(
                        text = "Highest",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val change = stats.totalChange
                    Text(
                        text = change?.let { "${if (it >= 0) "+" else ""}${String.format("%.1f", it)} kg" } ?: "--",
                        fontWeight = FontWeight.Bold,
                        color = if ((change ?: 0f) <= 0) Green500 else Pink500
                    )
                    Text(
                        text = "Total Change",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

/**
 * Milestone celebration card for weight loss achievements
 */
@Composable
fun MilestoneCelebrationCard(
    milestone: com.example.luminacal.data.repository.WeightMilestone
) {
    val gradientColors = listOf(
        Color(0xFFFFD700),
        Color(0xFFFFA500)
    )
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(Brush.horizontalGradient(gradientColors))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "🎉 Achievement Unlocked!",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = milestone.type.label,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Total: ${String.format("%.1f", milestone.kilosLost)} kg lost",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                Text(
                    text = milestone.type.emoji,
                    fontSize = 48.sp
                )
            }
        }
    }
}

