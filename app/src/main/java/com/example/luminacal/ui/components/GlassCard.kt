package com.example.luminacal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.luminacal.ui.theme.GlassWhite
import com.example.luminacal.ui.theme.GlassWhiteBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    active: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .background(
                if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            )
            .border(
                width = 1.dp,
                color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) 
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                shape = RoundedCornerShape(32.dp)
            )
            .padding(1.dp) // Subtle spacing for the edge
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}
