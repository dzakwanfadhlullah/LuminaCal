package com.example.luminacal.ui.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import kotlin.math.roundToInt
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.luminacal.R
import com.example.luminacal.model.DailyCalories
import com.example.luminacal.model.Macros
import com.example.luminacal.model.WeightPoint
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.ui.components.charts.MacroDistributionChart
import com.example.luminacal.ui.components.charts.WeeklyCalorieChart
import com.example.luminacal.ui.components.charts.WeightProgressChart
import com.example.luminacal.ui.theme.*

@Composable
fun StatisticsScreen(
    weeklyCalories: List<DailyCalories>,
    weightPoints: List<WeightPoint>,
    macros: Macros,
    currentWeight: Float,
    weightGoal: Float,
    loggingStreak: Int
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_calories),
        stringResource(R.string.tab_weight),
        stringResource(R.string.tab_macros)
    )
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column {
                Text(
                    text = stringResource(R.string.stats_analytics),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tab Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedTab == index) Blue500 else Color.Transparent)
                                .clickable { selectedTab = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (selectedTab == index) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Chart Item
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                when (selectedTab) {
                    0 -> { // Calories
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.stats_weekly_intake), fontWeight = FontWeight.Bold)
                                Text(stringResource(R.string.stats_avg_kcal, (weeklyCalories.map { it.calories }.average().toInt())), fontSize = 12.sp, color = Blue500)
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            WeeklyCalorieChart(
                                data = weeklyCalories,
                                modifier = Modifier.height(200.dp).fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                weeklyCalories.forEach {
                                    Text(it.day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                    1 -> { // Weight
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(stringResource(R.string.stats_weight_progress), fontWeight = FontWeight.Bold)
                                if (weightPoints.isNotEmpty()) {
                                    val change = weightPoints.last().weight - weightPoints.first().weight
                                    Text(
                                        text = "${if (change >= 0) "+" else ""}${String.format("%.1f", change)} kg",
                                        color = if (change <= 0) Green500 else Pink500,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            WeightProgressChart(
                                data = weightPoints,
                                modifier = Modifier.height(200.dp).fillMaxWidth()
                            )
                        }
                    }
                    2 -> { // Macros
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.stats_today_macros), fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                            Spacer(modifier = Modifier.height(24.dp))
                            MacroDistributionChart(
                                protein = macros.protein,
                                carbs = macros.carbs,
                                fat = macros.fat,
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                MacroIndicator(stringResource(R.string.macro_protein), "${macros.protein}g", Blue500)
                                MacroIndicator(stringResource(R.string.macro_carbs), "${macros.carbs}g", Green500)
                                MacroIndicator(stringResource(R.string.macro_fat), "${macros.fat}g", Peach400)
                            }
                        }
                    }
                }
            }
        }

        // Summary Section
        item {
            Text(stringResource(R.string.stats_goal_progress), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Goal Card 1: Weight Goal
                GlassCard(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.stats_target_weight), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${weightGoal.toInt()} kg", fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    // Calculate progress: how close current weight is to goal
                    val progress = if (currentWeight > weightGoal) {
                        // Losing weight: progress = how much lost / total to lose
                        val totalToLose = currentWeight - weightGoal
                        ((currentWeight - currentWeight) / totalToLose).coerceIn(0f, 1f)
                    } else if (currentWeight < weightGoal) {
                        // Gaining weight: progress = how much gained / total to gain
                        val totalToGain = weightGoal - currentWeight
                        ((currentWeight - currentWeight) / totalToGain).coerceIn(0f, 1f)
                    } else {
                        1f // Already at goal!
                    }
                    LinearProgressIndicator(
                        progress = { if (currentWeight == weightGoal) 1f else 0.5f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = Blue500,
                        trackColor = Blue500.copy(alpha = 0.1f)
                    )
                }

                // Goal Card 2: Streak
                GlassCard(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.stats_active_streak), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.stats_days_count, loggingStreak), fontSize = 20.sp, fontWeight = FontWeight.Black, color = Peach400)
                    Spacer(modifier = Modifier.height(12.dp))
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Peach400, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun MacroIndicator(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}
