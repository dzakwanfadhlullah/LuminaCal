package com.example.luminacal.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CelebrationOverlay(
    isVisible: Boolean,
    onFinished: () -> Unit
) {
    if (!isVisible) return

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "time"
    )

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(3000) // Show for 3 seconds
            onFinished()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val particles = 30
            repeat(particles) { i ->
                val progress = (time + (i.toFloat() / particles)) % 1f
                val angle = (i.toFloat() / particles) * 2 * Math.PI
                val distance = size.width * 0.4f * progress
                
                val x = (size.width / 2) + (Math.cos(angle).toFloat() * distance)
                val y = (size.height / 2) + (Math.sin(angle).toFloat() * distance)
                
                drawCircle(
                    color = Color.Yellow.copy(alpha = 1f - progress),
                    radius = 8f * (1f - progress),
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
    }
}
