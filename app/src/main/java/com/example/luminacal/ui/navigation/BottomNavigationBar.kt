package com.example.luminacal.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.R
import com.example.luminacal.ui.theme.Blue500
import com.example.luminacal.ui.theme.Slate900

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onScanClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
            .height(80.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Blue500.copy(alpha = 0.1f),
                spotColor = Blue500.copy(alpha = 0.1f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { screen ->
                if (screen == Screen.Camera) {
                    // Central Primary Action (Scan) with glow effect
                    val scanInteraction = remember { MutableInteractionSource() }
                    val isScanPressed by scanInteraction.collectIsPressedAsState()
                    
                    val scanScale by animateFloatAsState(
                        targetValue = if (isScanPressed) 0.9f else 1f,
                        animationSpec = spring(stiffness = 400f),
                        label = "scanScale"
                    )
                    
                    Box(
                        modifier = Modifier
                            .offset(y = (-15).dp)
                            .size(64.dp)
                            .scale(scanScale)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = Blue500.copy(alpha = 0.3f),
                                spotColor = Blue500.copy(alpha = 0.3f)
                            )
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.White.copy(alpha = 0.9f)
                                    )
                                )
                            )
                            .clickable(
                                interactionSource = scanInteraction,
                                indication = null
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onScanClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(screen.labelRes),
                            tint = Slate900,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    val isSelected = currentRoute == screen.route
                    val itemInteraction = remember { MutableInteractionSource() }
                    val isPressed by itemInteraction.collectIsPressedAsState()
                    
                    // Animated values for selection
                    val iconScale by animateFloatAsState(
                        targetValue = when {
                            isPressed -> 0.85f
                            isSelected -> 1.1f
                            else -> 1f
                        },
                        animationSpec = spring(stiffness = 400f),
                        label = "iconScale"
                    )
                    
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        animationSpec = spring(),
                        label = "iconColor"
                    )
                    
                    val bgAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 0.15f else 0f,
                        animationSpec = spring(),
                        label = "bgAlpha"
                    )
                    
                    val indicatorWidth by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 0.dp,
                        animationSpec = spring(stiffness = 300f),
                        label = "indicatorWidth"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = itemInteraction,
                                indication = null
                            ) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigate(screen.route)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Blue500.copy(alpha = bgAlpha))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = stringResource(screen.labelRes),
                                tint = iconColor,
                                modifier = Modifier
                                    .size(22.dp)
                                    .scale(iconScale)
                            )
                        }
                        
                        // Animated indicator dot/line
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(indicatorWidth)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    brush = if (isSelected) {
                                        Brush.horizontalGradient(
                                            colors = listOf(
                                                Blue500.copy(alpha = 0.5f),
                                                Blue500,
                                                Blue500.copy(alpha = 0.5f)
                                            )
                                        )
                                    } else {
                                        Brush.horizontalGradient(
                                            colors = listOf(Color.Transparent, Color.Transparent)
                                        )
                                    }
                                )
                        )
                        
                        // Label with slide animation
                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn() + slideInVertically { it },
                            exit = fadeOut() + slideOutVertically { it }
                        ) {
                            Text(
                                text = stringResource(screen.labelRes),
                                color = Blue500,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
