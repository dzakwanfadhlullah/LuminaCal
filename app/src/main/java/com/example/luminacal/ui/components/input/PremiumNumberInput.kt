package com.example.luminacal.ui.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.ui.theme.Blue500

/**
 * Premium Number Input with +/- stepper buttons
 */
@Composable
fun PremiumNumberInput(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    suffix: String = "",
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE,
    step: Int = 1,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Minus Button
            StepperButton(
                icon = Icons.Default.Remove,
                onClick = {
                    if (value > minValue) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onValueChange((value - step).coerceAtLeast(minValue))
                    }
                },
                enabled = enabled && value > minValue
            )
            
            // Value Display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = value.toString(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                if (suffix.isNotEmpty()) {
                    Text(
                        text = suffix,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            // Plus Button
            StepperButton(
                icon = Icons.Default.Add,
                onClick = {
                    if (value < maxValue) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onValueChange((value + step).coerceAtMost(maxValue))
                    }
                },
                enabled = enabled && value < maxValue
            )
        }
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(),
        label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Blue500 else Blue500.copy(alpha = 0.1f),
        animationSpec = spring(),
        label = "backgroundColor"
    )
    
    val iconColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            isPressed -> Color.White
            else -> Blue500
        },
        animationSpec = spring(),
        label = "iconColor"
    )
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (enabled) backgroundColor else Color.Gray.copy(alpha = 0.1f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}
