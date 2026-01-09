package com.example.luminacal.ui.components.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.ui.theme.Blue500

/**
 * Premium Glass Search Bar with expandable animation
 */
@Composable
fun GlassSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search...",
    onSearch: (String) -> Unit = {},
    onClear: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    // Animated values
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) Blue500 else Color.White.copy(alpha = 0.1f),
        animationSpec = spring(),
        label = "borderColor"
    )
    
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        animationSpec = spring(),
        label = "borderWidth"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isFocused) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        animationSpec = spring(),
        label = "iconColor"
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
    
    // Haptic on focus
    LaunchedEffect(isFocused) {
        if (isFocused) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(26.dp)
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Search Icon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Text Input
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
                interactionSource = interactionSource,
                cursorBrush = SolidColor(Blue500),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
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
            
            // Clear Button
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onQueryChange("")
                        onClear()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
