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
    
    val xOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "x1"
    )
    
    val yOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "y1"
    )

    val bgColor = if (darkMode) Slate950 else Slate50
    val blob1Color = if (darkMode) Color(0xFF4C1D95).copy(alpha = 0.3f) else Color(0xFFFFEDD5)
    val blob2Color = if (darkMode) Color(0xFF1E3A8A).copy(alpha = 0.3f) else Color(0xFFE0F2FE)
    val blob3Color = if (darkMode) Color(0xFF831843).copy(alpha = 0.3f) else Color(0xFFFCE7F3)

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = bgColor)
        
        // Blob 1
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(blob1Color, Color.Transparent),
                center = Offset(size.width * 0.2f + xOffset1, size.height * 0.2f + yOffset1),
                radius = size.width * 0.6f
            ),
            radius = size.width * 0.6f,
            center = Offset(size.width * 0.2f + xOffset1, size.height * 0.2f + yOffset1)
        )
        
        // Blob 2
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(blob2Color, Color.Transparent),
                center = Offset(size.width * 0.8f - xOffset1, size.height * 0.5f - yOffset1),
                radius = size.width * 0.7f
            ),
            radius = size.width * 0.7f,
            center = Offset(size.width * 0.8f - xOffset1, size.height * 0.5f - yOffset1)
        )
        
        // Blob 3
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(blob3Color, Color.Transparent),
                center = Offset(size.width * 0.4f, size.height * 0.8f + yOffset1),
                radius = size.width * 0.6f
            ),
            radius = size.width * 0.6f,
            center = Offset(size.width * 0.4f, size.height * 0.8f + yOffset1)
        )
    }
}
