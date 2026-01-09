package com.example.luminacal.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.example.luminacal.R
import com.example.luminacal.model.CalorieState
import com.example.luminacal.model.HealthMetrics
import com.example.luminacal.model.HistoryEntry
import com.example.luminacal.model.Macros
import com.example.luminacal.model.MealType
import com.example.luminacal.model.WaterState
import com.example.luminacal.ui.components.*
import com.example.luminacal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    isLoading: Boolean = false,
    calorieState: CalorieState,
    macros: Macros,
    healthMetrics: HealthMetrics = HealthMetrics(),
    history: List<HistoryEntry>,
    waterState: WaterState,
    loggingStreak: Int = 0,
    weeklyCalories: List<com.example.luminacal.model.DailyCalories> = emptyList(),
    onAddWater: (Int) -> Unit,
    onLogClick: (HistoryEntry) -> Unit,
    onDeleteMeal: (HistoryEntry) -> Unit = {},
    onProfileClick: () -> Unit,
    onViewAllClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedMacroLabel by remember { mutableStateOf<String?>(null) }
    var showCelebration by remember { mutableStateOf(false) }
    
    // Check for goal completion
    val isCalorieGoalMet = calorieState.consumed >= calorieState.target && calorieState.target > 0
    val isWaterGoalMet = waterState.consumed >= waterState.target && waterState.target > 0
    
    var lastCalorieMet by remember { mutableStateOf(false) }
    var lastWaterMet by remember { mutableStateOf(false) }

    LaunchedEffect(isCalorieGoalMet, isWaterGoalMet) {
        if ((isCalorieGoalMet && !lastCalorieMet) || (isWaterGoalMet && !lastWaterMet)) {
            showCelebration = true
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
        lastCalorieMet = isCalorieGoalMet
        lastWaterMet = isWaterGoalMet
    }
    
    // Show loading indicator
    if (isLoading) {
        DashboardSkeleton()
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.dashboard_today),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (loggingStreak > 0) {
                            Spacer(modifier = Modifier.width(12.dp))
                            Surface(
                                color = WarningBgLight,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Whatshot,
                                        contentDescription = "Streak",
                                        tint = WarningAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$loggingStreak",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = WarningAmber,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                
                AsyncImage(
                    model = "https://api.dicebear.com/7.x/avataaars/svg?seed=${healthMetrics.avatarSeed}",
                    contentDescription = stringResource(R.string.cd_profile_image),
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), CircleShape)
                        .clickable { 
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onProfileClick() 
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Main Ring Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Calculate dynamic status based on calorie intake
                    val consumedPercent = if (calorieState.target > 0) {
                        calorieState.consumed.toFloat() / calorieState.target
                    } else 0f
                    
                    val (statusText, statusColor, statusIcon) = when {
                        consumedPercent > 1.1f -> Triple("Over Budget", StatusError, Icons.AutoMirrored.Filled.TrendingUp)
                        consumedPercent < 0.3f && calorieState.consumed > 0 -> Triple("Getting Started", StatusWarning, Icons.AutoMirrored.Filled.TrendingUp)
                        consumedPercent < 0.1f -> Triple("Start Logging", NeutralGray, Icons.AutoMirrored.Filled.TrendingUp)
                        else -> Triple("On Track", StatusSuccess, Icons.AutoMirrored.Filled.TrendingUp)
                    }
                    
                    // Status Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.1f))
                            .border(1.dp, statusColor.copy(alpha = 0.2f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = statusText,
                            tint = statusColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppleRing(
                        progress = calorieState.consumed.toFloat() / calorieState.target,
                        labelProvider = {
                            AnimatedNumber(
                                value = calorieState.target - calorieState.consumed,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        subLabel = stringResource(R.string.dashboard_remaining),
                        icon = Icons.Default.Whatshot,
                        color = HighlightPeach
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Macros Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        listOf(
                            Triple(stringResource(R.string.macro_protein), macros.protein, MacroProtein),
                            Triple(stringResource(R.string.macro_carbs), macros.carbs, MacroCarbs),
                            Triple(stringResource(R.string.macro_fat), macros.fat, MacroFat)
                        ).forEach { (label, value, color) ->
                            MacroProgressBar(
                                label = label,
                                value = value,
                                max = when(label) { 
                                    stringResource(R.string.macro_protein) -> healthMetrics.recommendedProtein
                                    stringResource(R.string.macro_carbs) -> healthMetrics.recommendedCarbs
                                    else -> healthMetrics.recommendedFat 
                                },
                                color = color,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        selectedMacroLabel = if (selectedMacroLabel == label) null else label
                                    }
                            )
                        }
                    }
                    
                    AnimatedContent(
                        targetState = selectedMacroLabel,
                        transitionSpec = {
                            (slideInVertically { height -> height } + fadeIn()).togetherWith(
                                slideOutVertically { height -> -height } + fadeOut()
                            ).using(
                                SizeTransform(clip = false)
                            )
                        },
                        label = "macro_detail"
                    ) { label ->
                        if (label != null) {
                            Text(
                                text = when(label) {
                                    stringResource(R.string.macro_protein) -> stringResource(R.string.macro_protein_hint, (healthMetrics.recommendedProtein - macros.protein).coerceAtLeast(0))
                                    stringResource(R.string.macro_carbs) -> stringResource(R.string.macro_carbs_hint, macros.carbs, healthMetrics.recommendedCarbs)
                                    stringResource(R.string.macro_fat) -> stringResource(R.string.macro_fat_hint, macros.fat, healthMetrics.recommendedFat)
                                    else -> ""
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))

                        // Weekly Trend Chart Integration
                        if (weeklyCalories.isNotEmpty()) {
                            com.example.luminacal.ui.components.charts.WeeklyCalorieChart(
                                data = weeklyCalories,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        }
                    }
                }
                }
            }
        }

        // Water Tracking Widget
        item {
            WaterTrackingWidget(
                waterState = waterState,
                onAddWater = onAddWater
            )
        }

        // Timeline Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_recent_logs),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.dashboard_view_all),
                    style = MaterialTheme.typography.labelMedium,
                    color = Blue500,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onViewAllClick() 
                    }
                )
            }
        }

        // Empty State or Timeline Logs
        if (history.isEmpty()) {
            item {
                EmptyStateCard(
                    icon = Icons.Default.Restaurant,
                    title = stringResource(R.string.dashboard_no_meals),
                    subtitle = stringResource(R.string.dashboard_no_meals_subtitle)
                )
            }
        }
        
        // Timeline Logs (only if we have history)
        if (history.isNotEmpty()) {
            itemsIndexed(history, key = { _, entry -> entry.id }) { index, entry ->
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                            onDeleteMeal(entry)
                            true
                        } else false
                    }
                )
                
                SwipeToDismissBox(
                    state = dismissState,
                    backgroundContent = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DeleteRed)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White
                            )
                        }
                    },
                    enableDismissFromStartToEnd = false,
                    enableDismissFromEndToStart = true
                ) {
            Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)) {
                // Vertical Connector Line
                if (index < history.size - 1) {
                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 40.dp)
                            .width(2.dp)
                            .height(40.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Blue500.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )
                }

                GlassCard(
                    modifier = Modifier.padding(start = 8.dp).fillMaxWidth(),
                    onClick = { onLogClick(entry) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon Circle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when (entry.type) {
                                        MealType.BREAKFAST -> MealBreakfastBgLight
                                        MealType.LUNCH -> MealLunchBgLight
                                        MealType.SNACK -> MealSnackBgLight
                                        else -> MealDinnerBgLight
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (entry.type) {
                                    MealType.BREAKFAST -> "🍳"
                                    MealType.LUNCH -> "🥗"
                                    MealType.SNACK -> "🍎"
                                    else -> "🍽️"
                                },
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                            val timeDiff = System.currentTimeMillis() - entry.timestamp
                            val timeText = when {
                                timeDiff < 60_000 -> stringResource(R.string.time_just_now)
                                timeDiff < 3600_000 -> stringResource(R.string.time_min_ago, timeDiff / 60_000)
                                else -> {
                                    val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
                                    sdf.format(java.util.Date(entry.timestamp))
                                }
                            }
                            
                            val mealTypeParams = when(entry.type) {
                                MealType.BREAKFAST -> stringResource(R.string.meal_breakfast)
                                MealType.LUNCH -> stringResource(R.string.meal_lunch)
                                MealType.DINNER -> stringResource(R.string.meal_dinner)
                                MealType.SNACK -> stringResource(R.string.meal_snack)
                            }
                            
                            Text(
                                text = "$timeText • $mealTypeParams",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = entry.calories.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "kcal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                        }
                    }
                }
                }
            }
        }
    }
    
    // Celebration Layer
    CelebrationOverlay(
        isVisible = showCelebration,
        onFinished = { showCelebration = false }
    )
}
}
