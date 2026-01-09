package com.example.luminacal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shimmer effect for loading placeholders
 * Creates a smooth animated gradient that moves across the surface
 */
@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    baseColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
    highlightColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
) {
    val shimmerColors = listOf(
        baseColor,
        highlightColor,
        baseColor
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation - 200f, 0f),
        end = Offset(translateAnimation, 0f)
    )
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

/**
 * Image placeholder with shimmer while loading
 */
@Composable
fun ImageShimmerPlaceholder(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    ShimmerEffect(
        modifier = modifier,
        cornerRadius = cornerRadius
    )
}

/**
 * Text placeholder with shimmer (for skeleton loading)
 */
@Composable
fun TextShimmerPlaceholder(
    width: Dp = 100.dp,
    height: Dp = 16.dp,
    cornerRadius: Dp = 4.dp,
    modifier: Modifier = Modifier
) {
    ShimmerEffect(
        modifier = modifier
            .width(width)
            .height(height),
        cornerRadius = cornerRadius
    )
}

/**
 * Card placeholder with multiple shimmer elements
 */
@Composable
fun CardShimmerPlaceholder(
    modifier: Modifier = Modifier,
    imageHeight: Dp = 120.dp,
    cornerRadius: Dp = 16.dp
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        // Image placeholder
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            cornerRadius = 12.dp
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Title placeholder
        TextShimmerPlaceholder(width = 140.dp, height = 18.dp)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle placeholder
        TextShimmerPlaceholder(width = 80.dp, height = 14.dp)
    }
}

/**
 * Recipe card shimmer placeholder
 */
@Composable
fun RecipeCardShimmerPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Image
        ShimmerEffect(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            cornerRadius = 0.dp
        )
        
        Column(modifier = Modifier.padding(12.dp)) {
            // Category
            TextShimmerPlaceholder(width = 50.dp, height = 10.dp)
            Spacer(modifier = Modifier.height(4.dp))
            // Title
            TextShimmerPlaceholder(width = 100.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(4.dp))
            // Calories
            TextShimmerPlaceholder(width = 60.dp, height = 12.dp)
        }
    }
}
