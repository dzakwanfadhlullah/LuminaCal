package com.example.luminacal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shimmer effect colors for skeleton loading
 */
private val ShimmerColorShades = listOf(
    Color(0x33FFFFFF),
    Color(0x66FFFFFF),
    Color(0x33FFFFFF)
)

/**
 * Animated shimmer brush for skeleton loading effect
 */
@Composable
fun shimmerBrush(
    targetValue: Float = 1000f
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    
    return Brush.linearGradient(
        colors = ShimmerColorShades,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 200f, translateAnimation + 200f)
    )
}

/**
 * Skeleton loading card for dashboard
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    val brush = shimmerBrush()
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush)
        )
    }
}

/**
 * Skeleton row with title and value placeholder
 */
@Composable
fun SkeletonRow(
    modifier: Modifier = Modifier
) {
    val brush = shimmerBrush()
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Title placeholder
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
        
        // Value placeholder
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}

/**
 * Skeleton circle for avatars and icons
 */
@Composable
fun SkeletonCircle(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val brush = shimmerBrush()
    
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(brush)
    )
}

/**
 * Full dashboard skeleton loading state
 */
@Composable
fun DashboardSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                SkeletonRow()
            }
            SkeletonCircle(size = 40.dp)
        }
        
        // Main card skeleton
        SkeletonCard(height = 200.dp)
        
        // Stats row skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkeletonCard(
                modifier = Modifier.weight(1f),
                height = 100.dp
            )
            SkeletonCard(
                modifier = Modifier.weight(1f),
                height = 100.dp
            )
        }
        
        // Water widget skeleton
        SkeletonCard(height = 140.dp)
        
        // Meal list skeleton
        repeat(3) {
            SkeletonCard(height = 80.dp)
        }
    }
}

/**
 * Statistics screen skeleton
 */
@Composable
fun StatisticsSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab bar skeleton
        SkeletonCard(height = 44.dp)
        
        // Chart skeleton
        SkeletonCard(height = 220.dp)
        
        // Stats cards skeleton
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkeletonCard(
                modifier = Modifier.weight(1f),
                height = 100.dp
            )
            SkeletonCard(
                modifier = Modifier.weight(1f),
                height = 100.dp
            )
        }
    }
}

/**
 * Explore screen skeleton
 */
@Composable
fun ExploreSkeleton(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search bar skeleton
        SkeletonCard(height = 56.dp)
        
        // Filter chips skeleton
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(shimmerBrush())
                )
            }
        }
        
        // Food items skeleton
        repeat(5) {
            SkeletonCard(height = 90.dp)
        }
    }
}
