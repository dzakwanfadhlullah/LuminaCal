package com.example.luminacal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.luminacal.ui.theme.Slate900
import com.example.luminacal.ui.theme.Peach400
import com.example.luminacal.ui.theme.Pink500

enum class GlassButtonVariant {
    PRIMARY, SECONDARY, ACCENT, GHOST
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: GlassButtonVariant = GlassButtonVariant.PRIMARY,
    icon: ImageVector? = null
) {
    val backgroundBrush = when (variant) {
        GlassButtonVariant.PRIMARY -> Brush.horizontalGradient(listOf(Slate900, Color(0xFF111827)))
        GlassButtonVariant.ACCENT -> Brush.horizontalGradient(listOf(Peach400, Pink500))
        else -> null
    }

    val backgroundColor = when (variant) {
        GlassButtonVariant.SECONDARY -> Color.White.copy(alpha = 0.2f)
        GlassButtonVariant.GHOST -> Color.Transparent
        else -> Color.Transparent
    }

    val contentColor = when (variant) {
        GlassButtonVariant.PRIMARY, GlassButtonVariant.ACCENT -> Color.White
        GlassButtonVariant.SECONDARY -> MaterialTheme.colorScheme.onBackground
        GlassButtonVariant.GHOST -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    }

    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(if (backgroundBrush != null) Modifier.background(backgroundBrush) else Modifier.background(backgroundColor))
            .clickable { 
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick() 
            }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp).padding(end = 8.dp)
            )
        }
        Text(
            text = text,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
