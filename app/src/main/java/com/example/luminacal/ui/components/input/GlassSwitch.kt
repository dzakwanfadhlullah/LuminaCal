package com.example.luminacal.ui.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.example.luminacal.ui.theme.Blue500
import com.example.luminacal.ui.theme.Green500

/**
 * Premium Glass Switch with smooth animation and haptic feedback
 */
@Composable
fun GlassSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    enabled: Boolean = true,
    activeColor: Color = Blue500
) {
    val haptic = LocalHapticFeedback.current
    
    // Animated values
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 0.dp,
        animationSpec = spring(stiffness = 400f),
        label = "thumbOffset"
    )
    
    val trackColor by animateColorAsState(
        targetValue = if (checked) activeColor else Color.Gray.copy(alpha = 0.3f),
        animationSpec = spring(),
        label = "trackColor"
    )
    
    val thumbColor by animateColorAsState(
        targetValue = if (checked) Color.White else MaterialTheme.colorScheme.surface,
        animationSpec = spring(),
        label = "thumbColor"
    )
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Switch Track
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = if (checked) {
                        Brush.horizontalGradient(listOf(activeColor, activeColor.copy(alpha = 0.8f)))
                    } else {
                        Brush.horizontalGradient(listOf(trackColor, trackColor))
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (checked) activeColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = enabled
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCheckedChange(!checked)
                }
                .padding(2.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Thumb
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(thumbColor)
            )
        }
    }
}
