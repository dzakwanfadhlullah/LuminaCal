package com.example.luminacal.ui.components.input

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.ui.theme.Blue500

/**
 * Premium Glass TextField with blur effect and focus animation
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    // Animated values
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> Color(0xFFEF4444)
            isFocused -> Blue500
            else -> Color.White.copy(alpha = 0.1f)
        },
        animationSpec = spring(),
        label = "borderColor"
    )
    
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused || isError) 2.dp else 1.dp,
        animationSpec = spring(),
        label = "borderWidth"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(),
        label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        },
        animationSpec = spring(),
        label = "backgroundColor"
    )
    
    // Haptic feedback on focus
    LaunchedEffect(isFocused) {
        if (isFocused) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Column(modifier = modifier) {
        // Label
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (isError) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Text Field Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .border(
                    width = borderWidth,
                    brush = if (isFocused) {
                        Brush.linearGradient(listOf(Blue500, Blue500.copy(alpha = 0.5f)))
                    } else {
                        Brush.linearGradient(listOf(borderColor, borderColor))
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Leading Icon
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isFocused) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                // Text Input
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    singleLine = singleLine,
                    maxLines = maxLines,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    visualTransformation = visualTransformation,
                    interactionSource = interactionSource,
                    cursorBrush = SolidColor(Blue500),
                    decorationBox = { innerTextField ->
                        Box {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        fontSize = 16.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                
                // Trailing Icon
                if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(12.dp))
                    trailingIcon()
                }
            }
        }
        
        // Error Message
        if (isError && !errorMessage.isNullOrEmpty()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFEF4444),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}
