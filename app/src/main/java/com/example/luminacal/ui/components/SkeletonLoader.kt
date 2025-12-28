package com.example.luminacal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A modifier that adds a shimmering effect to any component, useful for skeleton loading.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.1f),
        Color.LightGray.copy(alpha = 0.3f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    background(brush)
}

@Composable
fun SkeletonItem(
    modifier: Modifier = Modifier,
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .then(if (width != Dp.Unspecified) Modifier.width(width) else Modifier)
            .then(if (height != Dp.Unspecified) Modifier.height(height) else Modifier)
            .clip(RoundedCornerShape(cornerRadius))
            .shimmerEffect()
    )
}

@Composable
fun DashboardSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column {
                SkeletonItem(width = 60.dp, height = 12.dp)
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonItem(width = 150.dp, height = 24.dp)
            }
            SkeletonItem(width = 40.dp, height = 40.dp, cornerRadius = 20.dp)
        }

        // Main Card Skeleton
        SkeletonItem(modifier = Modifier.fillMaxWidth(), height = 300.dp, cornerRadius = 24.dp)

        // Water/Weight Widgets Skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SkeletonItem(modifier = Modifier.weight(1f), height = 180.dp, cornerRadius = 24.dp)
            SkeletonItem(modifier = Modifier.weight(1f), height = 180.dp, cornerRadius = 24.dp)
        }

        // History Skeleton
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SkeletonItem(width = 100.dp, height = 16.dp)
            repeat(3) {
                SkeletonItem(modifier = Modifier.fillMaxWidth(), height = 80.dp, cornerRadius = 16.dp)
            }
        }
    }
}
