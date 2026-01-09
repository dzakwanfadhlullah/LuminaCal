package com.example.luminacal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.luminacal.R
import com.example.luminacal.model.BeverageType
import com.example.luminacal.model.GlassSize
import com.example.luminacal.model.WaterState
import androidx.compose.ui.platform.LocalDensity

@Composable
fun WaterTrackingWidget(
    waterState: WaterState,
    onAddWater: (Int) -> Unit,
    onAddWaterWithType: (Int, BeverageType) -> Unit = { ml, _ -> onAddWater(ml) },
    onUpdateTarget: (Int) -> Unit = {},
    customCupSizes: List<CustomCupSize> = defaultCupSizes,
    onUpdateCupSizes: (List<CustomCupSize>) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val progress = waterState.progress
    
    // Selected beverage type state
    var selectedBeverageType by remember { mutableStateOf(BeverageType.WATER) }
    
    // Settings dialog state
    var showSettingsDialog by remember { mutableStateOf(false) }
    
    // Animated progress
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "water_progress"
    )
    
    // Settings Dialog
    if (showSettingsDialog) {
        WaterSettingsDialog(
            currentTarget = waterState.target,
            customSizes = customCupSizes,
            onDismiss = { showSettingsDialog = false },
            onSave = { newTarget, newSizes ->
                onUpdateTarget(newTarget)
                onUpdateCupSizes(newSizes)
            }
        )
    }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFF3B82F6),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.water_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Cups display with goal indicator
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${String.format("%.1f", waterState.cupsConsumed)} / ${waterState.cupsTarget.toInt()} cups",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (waterState.goalReached) Color(0xFF22C55E) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        if (waterState.goalReached) {
                            Text(
                                text = "🎉 Goal reached!",
                                fontSize = 10.sp,
                                color = Color(0xFF22C55E)
                            )
                        }
                    }
                    
                    // Settings button
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showSettingsDialog = true
                        },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Water Settings",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Beverage Type Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BeverageType.entries.forEach { type ->
                    val isSelected = type == selectedBeverageType
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedBeverageType = type
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFF3B82F6).copy(alpha = 0.15f) else Color.Transparent
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = type.emoji,
                                fontSize = 16.sp
                            )
                            Text(
                                text = type.label,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color(0xFF3B82F6) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
            
            // Caffeine warning for coffee
            if (selectedBeverageType == BeverageType.COFFEE) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "☕ Tip: Coffee counts as 80% hydration due to caffeine",
                    fontSize = 10.sp,
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar with Wave Animation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "wave")
                val waveOffset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 2 * Math.PI.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "waveOffset"
                )

                val density = LocalDensity.current
                val waveHeight = with(density) { 4.dp.toPx() }

                Canvas(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    val waveFrequency = 0.05f
                    
                    val path = Path().apply {
                        moveTo(0f, size.height)
                        for (x in 0..size.width.toInt()) {
                            val y = (Math.sin((x * waveFrequency + waveOffset).toDouble()).toFloat() * waveHeight) + (size.height / 2f)
                            lineTo(x.toFloat(), y)
                        }
                        lineTo(size.width, size.height)
                        close()
                    }

                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF60A5FA), Color(0xFF3B82F6))
                        )
                    )
                    
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.2f)
                    )
                }
                
                // Text overlay
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${waterState.consumed}ml",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (progress > 0.3f) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${waterState.target}ml",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Glass size quick-add buttons (all sizes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GlassSize.entries.forEach { size ->
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onAddWaterWithType(size.amountMl, selectedBeverageType)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6).copy(alpha = 0.15f),
                            contentColor = Color(0xFF3B82F6)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = size.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

