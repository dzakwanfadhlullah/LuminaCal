package com.example.luminacal.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.luminacal.R
import com.example.luminacal.model.*
import com.example.luminacal.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: (HealthMetrics) -> Unit,
    onSkip: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    // State for collected data
    var gender by remember { mutableStateOf(Gender.MALE) }
    var age by remember { mutableIntStateOf(25) }
    var weight by remember { mutableFloatStateOf(70f) }
    var height by remember { mutableFloatStateOf(170f) }
    var activityLevel by remember { mutableStateOf(ActivityLevel.MODERATE) }
    var fitnessGoal by remember { mutableStateOf(FitnessGoal.MAINTAIN) }

    val healthMetrics = remember(weight, height, age, gender, activityLevel, fitnessGoal) {
        HealthMetrics(
            weight = weight,
            height = height,
            age = age,
            gender = gender,
            activityLevel = activityLevel,
            fitnessGoal = fitnessGoal
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Skip button
        if (pagerState.currentPage < 3) {
            TextButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSkip()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.onboarding_skip), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> AboutYouPage(
                        gender = gender,
                        onGenderChange = { gender = it },
                        age = age,
                        onAgeChange = { age = it }
                    )
                    2 -> BodyMetricsPage(
                        weight = weight,
                        onWeightChange = { weight = it },
                        height = height,
                        onHeightChange = { height = it }
                    )
                    3 -> GoalsPage(
                        activityLevel = activityLevel,
                        onActivityChange = { activityLevel = it },
                        fitnessGoal = fitnessGoal,
                        onGoalChange = { fitnessGoal = it },
                        tdee = healthMetrics.targetCalories
                    )
                }
            }

            // Page Indicators
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 24.dp else 8.dp, 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index) Blue500
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Back button
                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(R.string.onboarding_back))
                    }
                }

                // Next/Finish button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (pagerState.currentPage < 3) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onComplete(healthMetrics)
                        }
                    },
                    modifier = Modifier.weight(if (pagerState.currentPage > 0) 1f else 1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                ) {
                    Text(
                        if (pagerState.currentPage < 3) stringResource(R.string.onboarding_next) else stringResource(R.string.onboarding_start_tracking),
                        fontWeight = FontWeight.Bold
                    )
                    if (pagerState.currentPage < 3) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated logo
        val infiniteTransition = rememberInfiniteTransition(label = "logo")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(Blue500, Pink500))
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.onboarding_welcome),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = Blue500
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your AI-powered calorie tracker.\nSmarter eating starts here.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun AboutYouPage(
    gender: Gender,
    onGenderChange: (Gender) -> Unit,
    age: Int,
    onAgeChange: (Int) -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Tell us about you",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "This helps us personalize your experience",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Gender Selection
        Text(
            text = "Gender",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Gender.entries.forEach { g ->
                val isSelected = g == gender
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onGenderChange(g)
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Blue500.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) BorderStroke(2.dp, Blue500) else null
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = when (g) {
                                Gender.MALE -> Icons.Default.Male
                                Gender.FEMALE -> Icons.Default.Female
                                Gender.OTHER -> Icons.Default.Transgender
                            },
                            contentDescription = null,
                            tint = if (isSelected) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = g.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Blue500 else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Age Slider
        Text(
            text = "Age: $age years",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = age.toFloat(),
            onValueChange = { 
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onAgeChange(it.toInt()) 
            },
            valueRange = 15f..80f,
            colors = SliderDefaults.colors(
                thumbColor = Blue500,
                activeTrackColor = Blue500
            )
        )
    }
}

@Composable
fun BodyMetricsPage(
    weight: Float,
    onWeightChange: (Float) -> Unit,
    height: Float,
    onHeightChange: (Float) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    // BMI Validation
    val bmiValidation = remember(weight, height) {
        com.example.luminacal.util.ValidationUtils.validateBMI(weight, height)
    }
    val bmiValue = remember(weight, height) {
        if (height > 0) {
            val heightM = height / 100
            weight / (heightM * heightM)
        } else 0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Body Metrics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Used to calculate your daily needs",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Weight
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Blue500)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Weight", fontWeight = FontWeight.SemiBold)
                    }
                    Text("${weight.toInt()} kg", fontWeight = FontWeight.Bold, color = Blue500)
                }
                Slider(
                    value = weight,
                    onValueChange = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onWeightChange(it) 
                    },
                    valueRange = 40f..200f,
                    colors = SliderDefaults.colors(thumbColor = Blue500, activeTrackColor = Blue500)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Height
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Height, contentDescription = null, tint = Green500)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Height", fontWeight = FontWeight.SemiBold)
                    }
                    Text("${height.toInt()} cm", fontWeight = FontWeight.Bold, color = Green500)
                }
                Slider(
                    value = height,
                    onValueChange = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onHeightChange(it) 
                    },
                    valueRange = 120f..220f,
                    colors = SliderDefaults.colors(thumbColor = Green500, activeTrackColor = Green500)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // BMI Feedback Card
        val bmiColor = when {
            bmiValue < 18.5 -> Color(0xFFF59E0B) // Orange for underweight
            bmiValue < 25 -> Color(0xFF22C55E) // Green for normal
            bmiValue < 30 -> Color(0xFFF59E0B) // Orange for overweight
            else -> Color(0xFFEF4444) // Red for obese
        }
        val bmiStatus = when {
            bmiValue < 18.5 -> "Underweight"
            bmiValue < 25 -> "Normal"
            bmiValue < 30 -> "Overweight"
            else -> "Obese"
        }
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = bmiColor.copy(alpha = 0.1f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "BMI: ${String.format("%.1f", bmiValue)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = bmiColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "• $bmiStatus",
                    style = MaterialTheme.typography.bodyMedium,
                    color = bmiColor
                )
            }
        }
    }
}

@Composable
fun GoalsPage(
    activityLevel: ActivityLevel,
    onActivityChange: (ActivityLevel) -> Unit,
    fitnessGoal: FitnessGoal,
    onGoalChange: (FitnessGoal) -> Unit,
    tdee: Int
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Goals",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Activity Level
        Text(
            text = "Activity Level",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActivityLevel.entries.take(3).forEach { level ->
                val isSelected = level == activityLevel
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onActivityChange(level)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Green500.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) BorderStroke(2.dp, Green500) else null
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (level) {
                                ActivityLevel.SEDENTARY -> "🛋️"
                                ActivityLevel.LIGHT -> "🚶"
                                ActivityLevel.MODERATE -> "🏃"
                                else -> "🏋️"
                            },
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = level.label.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Green500 else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Fitness Goal
        Text(
            text = "Fitness Goal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FitnessGoal.entries.forEach { goal ->
                val isSelected = goal == fitnessGoal
                val color = when (goal) {
                    FitnessGoal.LOSE_WEIGHT -> Color(0xFFEF4444)
                    FitnessGoal.MAINTAIN -> Blue500
                    FitnessGoal.GAIN_MUSCLE -> Green500
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onGoalChange(goal)
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) BorderStroke(2.dp, color) else null
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
                            text = goal.label.split(" ").first(),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // TDEE Preview
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Blue500.copy(alpha = 0.1f)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Daily Target",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$tdee",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = Blue500
                )
                Text(
                    text = "calories per day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
