package com.example.luminacal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.model.HydrationAchievement
import com.example.luminacal.model.HydrationStreak
import com.example.luminacal.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Card displaying hydration streak and achievement badges
 */
@Composable
fun HydrationStreakCard(
    streak: HydrationStreak,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (streak.hasActiveStreak) {
        listOf(Color(0xFF3B82F6), Color(0xFF8B5CF6))
    } else {
        listOf(Color(0xFF6B7280), Color(0xFF9CA3AF))
    }
    
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = if (streak.hasActiveStreak) Color(0xFFF59E0B) else Color(0xFF9CA3AF),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hydration Streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Streak protection indicator
                if (streak.streakProtectionUsed) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Protected",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Streak counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Current streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${streak.currentStreak}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = if (streak.hasActiveStreak) Color(0xFF3B82F6) else Color(0xFF9CA3AF)
                    )
                    Text(
                        text = "Current",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
                
                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                
                // Longest streak
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${streak.longestStreak}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFF59E0B)
                    )
                    Text(
                        text = "Best",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Current achievement badge
            streak.currentAchievement?.let { achievement ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF3B82F6).copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = achievement.emoji,
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = achievement.title,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                            Text(
                                text = achievement.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
            
            // Next achievement progress
            streak.nextAchievement?.let { nextAchievement ->
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Next: ${nextAchievement.emoji} ${nextAchievement.title}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${streak.daysToNextAchievement} days to go",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3B82F6)
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Progress bar to next achievement
                val progressToNext = streak.currentStreak.toFloat() / nextAchievement.requiredDays
                LinearProgressIndicator(
                    progress = { progressToNext },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = Color(0xFF3B82F6),
                    trackColor = Color(0xFF3B82F6).copy(alpha = 0.1f)
                )
            }
        }
    }
}

/**
 * Confetti celebration overlay when water goal is reached
 */
@Composable
fun GoalCelebrationOverlay(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    
    // Create multiple confetti particles
    val particles = remember {
        List(30) {
            ConfettiParticle(
                x = Random.nextFloat(),
                startY = Random.nextFloat() * -0.5f,
                color = listOf(
                    Color(0xFF3B82F6),
                    Color(0xFF22C55E),
                    Color(0xFFF59E0B),
                    Color(0xFFEC4899),
                    Color(0xFF8B5CF6)
                ).random(),
                size = Random.nextFloat() * 8f + 4f,
                speed = Random.nextFloat() * 0.3f + 0.2f,
                rotation = Random.nextFloat() * 360f
            )
        }
    }
    
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_fall"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = ((particle.startY + animatedProgress * (1f + particle.speed)) % 1.2f) * size.height
            val x = particle.x * size.width + sin(animatedProgress * 10 + particle.rotation) * 20
            
            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val startY: Float,
    val color: Color,
    val size: Float,
    val speed: Float,
    val rotation: Float
)

/**
 * Bubble particles animation for water widget
 */
@Composable
fun BubbleParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 15
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bubbles")
    
    val bubbles = remember {
        List(particleCount) {
            Bubble(
                x = Random.nextFloat(),
                startY = Random.nextFloat(),
                size = Random.nextFloat() * 6f + 2f,
                speed = Random.nextFloat() * 0.4f + 0.2f,
                wobble = Random.nextFloat() * 10f
            )
        }
    }
    
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubble_rise"
    )
    
    Canvas(modifier = modifier) {
        bubbles.forEach { bubble ->
            // Bubbles rise from bottom to top
            val y = size.height - ((bubble.startY + animatedProgress * bubble.speed) % 1f) * size.height
            val x = bubble.x * size.width + sin(animatedProgress * 5 + bubble.wobble) * 10
            
            // Draw bubble with slight transparency
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = bubble.size,
                center = Offset(x, y)
            )
            // Inner highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = bubble.size * 0.4f,
                center = Offset(x - bubble.size * 0.2f, y - bubble.size * 0.2f)
            )
        }
    }
}

private data class Bubble(
    val x: Float,
    val startY: Float,
    val size: Float,
    val speed: Float,
    val wobble: Float
)
