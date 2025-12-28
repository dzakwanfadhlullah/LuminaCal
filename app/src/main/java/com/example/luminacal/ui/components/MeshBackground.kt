package com.example.luminacal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.luminacal.ui.theme.Slate50
import com.example.luminacal.ui.theme.Slate950

@Composable
fun MeshBackground(darkMode: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    
    // Complex animation patterns
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = Math.PI.toFloat() * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "time"
    )

    val bgColor = if (darkMode) Slate950 else Slate50
    val blobColors = if (darkMode) {
        listOf(
            Color(0xFF4C1D95).copy(alpha = 0.4f), // Deep Purple
            Color(0xFF1E3A8A).copy(alpha = 0.3f), // Royal Blue
            Color(0xFF831843).copy(alpha = 0.3f), // Maroon
            Color(0xFF064E3B).copy(alpha = 0.2f), // Emerald
            Color(0xFF701A75).copy(alpha = 0.3f)  // Fuchsia
        )
    } else {
        listOf(
            Color(0xFFFFEDD5), // Orange tint
            Color(0xFFE0F2FE), // Blue tint
            Color(0xFFFCE7F3), // Pink tint
            Color(0xFFF0FDF4), // Green tint
            Color(0xFFFAF5FF)  // Purple tint
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = bgColor)
        
        // Dynamic Blobs
        val blobData = listOf(
            // Center-Left Blob (Spiral)
            Triple(0.2f, 0.3f, 0.6f),
            // Top-Right Blob (Swaying)
            Triple(0.8f, 0.2f, 0.7f),
            // Bottom-Mid Blob (Floating)
            Triple(0.5f, 0.8f, 0.6f),
            // Far-Bottom-Left (Drifting)
            Triple(0.1f, 0.9f, 0.5f),
            // Upper-Mid (Pulse)
            Triple(0.6f, 0.4f, 0.5f)
        )

        blobData.forEachIndexed { index, (baseX, baseY, radiusMult) ->
            val phase = index * (Math.PI.toFloat() / 2.5f)
            val currentX = baseX + (Math.cos((time + phase).toDouble()).toFloat() * 0.15f)
            val currentY = baseY + (Math.sin((time * 0.7 + phase).toDouble()).toFloat() * 0.2f)
            
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(blobColors[index % blobColors.size], Color.Transparent),
                    center = Offset(size.width * currentX, size.height * currentY),
                    radius = size.width * radiusMult
                ),
                radius = size.width * radiusMult,
                center = Offset(size.width * currentX, size.height * currentY)
            )
        }
    }
}
