package com.example.luminacal.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

/**
 * Animation durations constants (Apple HIG style - consistent 300ms)
 */
object AnimationDurations {
    const val SHORT = 150
    const val STANDARD = 300
    const val MEDIUM = 400
    const val LONG = 500
}

/**
 * Button with scale animation on press (0.95x) and haptic feedback
 */
@Composable
fun AnimatedPressButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )
    
    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.scale(scale),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

/**
 * Pressable box with scale animation for custom clickable elements
 */
@Composable
fun PressableBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    scaleAmount: Float = 0.96f,
    content: @Composable BoxScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleAmount else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "pressable_scale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .pointerInput(enabled) {
                detectTapGestures(
                    onPress = {
                        if (enabled) {
                            isPressed = true
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            tryAwaitRelease()
                            isPressed = false
                        }
                    },
                    onTap = {
                        if (enabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onClick()
                        }
                    }
                )
            },
        content = content
    )
}

/**
 * Staggered animation for list items
 */
@Composable
fun StaggeredAnimatedItem(
    index: Int,
    modifier: Modifier = Modifier,
    delayPerItem: Int = 50,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * delayPerItem).toLong())
        visible = true
    }
    
    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stagger_offset"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(AnimationDurations.STANDARD),
        label = "stagger_alpha"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationY = offsetY
                this.alpha = alpha
            }
    ) {
        content()
    }
}

/**
 * Fade + Slide entrance animation for page content
 */
@Composable
fun FadeSlideEnterAnimation(
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(AnimationDurations.STANDARD)
        ) + slideInVertically(
            initialOffsetY = { it / 10 },
            animationSpec = tween(AnimationDurations.STANDARD)
        ),
        content = content
    )
}

/**
 * Pulse animation for highlighting updated elements
 */
@Composable
fun PulseAnimation(
    trigger: Any,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isPulsing by remember { mutableStateOf(false) }
    
    LaunchedEffect(trigger) {
        isPulsing = true
        kotlinx.coroutines.delay(300)
        isPulsing = false
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isPulsing) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "pulse_scale"
    )
    
    Box(modifier = modifier.scale(scale)) {
        content()
    }
}

/**
 * Glow effect for completed states (like AppleRing at 100%)
 */
@Composable
fun GlowOnComplete(
    isComplete: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    Box(
        modifier = modifier.graphicsLayer {
            if (isComplete) {
                // Create subtle glow effect when complete
                shadowElevation = 8f * glowAlpha
            }
        }
    ) {
        content()
    }
}

/**
 * Swipe-to-delete animation state holder
 */
@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val haptic = LocalHapticFeedback.current
    
    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "swipe_offset"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                translationX = animatedOffset
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        // Reset on press
                        offsetX = 0f
                    }
                )
            }
    ) {
        content()
    }
}

/**
 * Number counter animation (for calories, steps, etc.)
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    val animatedValue by animateIntAsState(
        targetValue = targetValue,
        animationSpec = tween(
            durationMillis = AnimationDurations.MEDIUM,
            easing = FastOutSlowInEasing
        ),
        label = "counter"
    )
    
    Box(modifier = modifier) {
        content(animatedValue)
    }
}
