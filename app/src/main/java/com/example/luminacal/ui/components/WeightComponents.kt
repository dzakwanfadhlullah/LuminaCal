package com.example.luminacal.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.stringResource
import com.example.luminacal.R
import com.example.luminacal.data.repository.WeightEntry
import com.example.luminacal.data.repository.WeightTrend
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeightEntryCard(
    entry: WeightEntry,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Weight icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E7FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MonitorWeight,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = String.format("%.1f kg", entry.weightKg),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateFormatter.format(Date(entry.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                entry.note?.let { note ->
                    Text(
                        text = note,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDelete()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun WeightTrendBadge(
    trend: WeightTrend,
    modifier: Modifier = Modifier
) {
    val change = trend.weeklyChange
    
    if (change == null) return

    data class TrendInfo(val icon: ImageVector, val color: Color, val labelResId: Int?, val textSuffix: String?)
    
    val (icon, color, labelResId, textSuffix) = when {
        change > 0.1f -> TrendInfo(Icons.Default.TrendingUp, Color(0xFFEF4444), null, "+${String.format("%.1f", change)} kg")
        change < -0.1f -> TrendInfo(Icons.Default.TrendingDown, Color(0xFF22C55E), null, "${String.format("%.1f", change)} kg")
        else -> TrendInfo(Icons.Default.TrendingFlat, Color(0xFF6B7280), R.string.no_change, null)
    }

    val displayText = if (labelResId != null) stringResource(labelResId) else textSuffix ?: ""

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
fun AddWeightDialog(
    currentWeight: Float,
    onDismiss: () -> Unit,
    onConfirm: (Float, String?) -> Unit
) {
    var weightText by remember { mutableStateOf(String.format("%.1f", currentWeight)) }
    var noteText by remember { mutableStateOf("") }
    val haptic = LocalHapticFeedback.current
    
    // Validation state
    val weightValue = weightText.toFloatOrNull()
    val validationResult = remember(weightText) {
        weightValue?.let { com.example.luminacal.util.ValidationUtils.validateWeight(it) }
    }
    val isValid = weightValue != null && (validationResult?.isValid == true)
    val hasError = validationResult?.errorMessage != null
    val hasWarning = validationResult?.warningMessage != null

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.add_weight),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text(stringResource(R.string.health_weight) + " (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.MonitorWeight, contentDescription = null)
                    },
                    isError = hasError || weightValue == null,
                    supportingText = {
                        when {
                            weightValue == null && weightText.isNotEmpty() -> 
                                Text(stringResource(R.string.invalid_number), color = MaterialTheme.colorScheme.error)
                            hasError -> 
                                Text(validationResult?.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                            hasWarning -> 
                                Text(validationResult?.warningMessage ?: "", color = Color(0xFFFB923C))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text(stringResource(R.string.weight_note_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Notes, contentDescription = null)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val weight = weightValue ?: currentWeight
                            onConfirm(weight, noteText.ifBlank { null })
                        },
                        modifier = Modifier.weight(1f),
                        enabled = isValid
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}
