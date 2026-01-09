package com.example.luminacal.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.luminacal.ui.theme.Blue500

/**
 * Data class for customizable water cup size
 */
data class CustomCupSize(
    val id: Int,
    val amountMl: Int,
    val label: String,
    val emoji: String = "🥤"
)

/**
 * Default preset cup sizes
 */
val defaultCupSizes = listOf(
    CustomCupSize(1, 150, "Small", "🥛"),
    CustomCupSize(2, 250, "Medium", "🥤"),
    CustomCupSize(3, 350, "Large", "🍶"),
    CustomCupSize(4, 500, "Bottle", "🧴")
)

/**
 * Water Settings Dialog for customizing cup sizes and daily goal
 */
@Composable
fun WaterSettingsDialog(
    currentTarget: Int,
    customSizes: List<CustomCupSize>,
    onDismiss: () -> Unit,
    onSave: (newTarget: Int, newSizes: List<CustomCupSize>) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    var dailyGoal by remember { mutableStateOf(currentTarget.toString()) }
    var editingSizes by remember { mutableStateOf(customSizes.ifEmpty { defaultCupSizes }) }
    var showAddSizeDialog by remember { mutableStateOf(false) }
    var editingSize by remember { mutableStateOf<CustomCupSize?>(null) }
    
    // Add/Edit Size Dialog
    if (showAddSizeDialog || editingSize != null) {
        CupSizeEditDialog(
            existingSize = editingSize,
            onDismiss = { 
                showAddSizeDialog = false
                editingSize = null
            },
            onSave = { newSize ->
                if (editingSize != null) {
                    editingSizes = editingSizes.map { if (it.id == editingSize!!.id) newSize else it }
                } else {
                    val newId = (editingSizes.maxOfOrNull { it.id } ?: 0) + 1
                    editingSizes = editingSizes + newSize.copy(id = newId)
                }
                showAddSizeDialog = false
                editingSize = null
            }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Water Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Daily Goal Section
                Text(
                    text = "Daily Goal",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = dailyGoal,
                        onValueChange = { 
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                dailyGoal = it
                            }
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        suffix = { Text("ml") },
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Quick preset buttons
                    Column {
                        listOf(2000, 2500, 3000).forEach { preset ->
                            TextButton(
                                onClick = { 
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    dailyGoal = preset.toString() 
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${preset / 1000}L",
                                    fontSize = 12.sp,
                                    color = if (dailyGoal == preset.toString()) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Cup Sizes Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Add Sizes",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    if (editingSizes.size < 6) {
                        TextButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showAddSizeDialog = true
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontSize = 12.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Cup size chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(editingSizes) { size ->
                        CupSizeChip(
                            size = size,
                            onEdit = { editingSize = size },
                            onDelete = if (editingSizes.size > 1) {
                                { editingSizes = editingSizes.filter { it.id != size.id } }
                            } else null
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Save Button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val newTarget = dailyGoal.toIntOrNull() ?: currentTarget
                        onSave(newTarget.coerceIn(500, 10000), editingSizes)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Settings", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CupSizeChip(
    size: CustomCupSize,
    onEdit: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val haptic = LocalHapticFeedback.current
    
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { 
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onEdit() 
            },
        color = Blue500.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = size.emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = size.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Blue500
                )
                Text(
                    text = "${size.amountMl}ml",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            if (onDelete != null) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDelete()
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CupSizeEditDialog(
    existingSize: CustomCupSize?,
    onDismiss: () -> Unit,
    onSave: (CustomCupSize) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    
    var label by remember { mutableStateOf(existingSize?.label ?: "") }
    var amount by remember { mutableStateOf(existingSize?.amountMl?.toString() ?: "") }
    var selectedEmoji by remember { mutableStateOf(existingSize?.emoji ?: "🥤") }
    
    val emojiOptions = listOf("💧", "🥛", "🥤", "🍶", "🧴", "☕", "🍵", "🧃")
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (existingSize != null) "Edit Cup Size" else "Add Cup Size",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Emoji selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    emojiOptions.forEach { emoji ->
                        val isSelected = emoji == selectedEmoji
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Blue500.copy(alpha = 0.2f) else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it.take(10) },
                    label = { Text("Label") },
                    placeholder = { Text("e.g. My Bottle") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                            amount = it.take(4)
                        }
                    },
                    label = { Text("Amount") },
                    suffix = { Text("ml") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    
                    Button(
                        onClick = {
                            val amountMl = amount.toIntOrNull() ?: return@Button
                            if (label.isNotBlank() && amountMl in 50..2000) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onSave(CustomCupSize(
                                    id = existingSize?.id ?: 0,
                                    amountMl = amountMl,
                                    label = label.trim(),
                                    emoji = selectedEmoji
                                ))
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
