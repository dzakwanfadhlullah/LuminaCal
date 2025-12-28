package com.example.luminacal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import com.example.luminacal.ui.theme.GlassWhiteBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    active: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(32.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null, // Custom scale indication
                        onClick = onClick
                    )
                } else Modifier
            )
            .background(
                if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) 
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = if (active) {
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.05f))
                    }
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
