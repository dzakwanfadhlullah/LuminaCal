package com.example.luminacal.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.luminacal.R
import com.example.luminacal.ui.theme.Blue500
import com.example.luminacal.ui.theme.Peach400

/**
 * Dialog for adding or editing custom foods
 */
@Composable
fun AddCustomFoodDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, calories: Int, protein: Int, carbs: Int, fat: Int, servingSize: String) -> Unit,
    // For edit mode
    initialName: String = "",
    initialCalories: Int = 0,
    initialProtein: Int = 0,
    initialCarbs: Int = 0,
    initialFat: Int = 0,
    initialServingSize: String = "1 serving",
    isEditMode: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    
    var name by remember { mutableStateOf(initialName) }
    var caloriesText by remember { mutableStateOf(if (initialCalories > 0) initialCalories.toString() else "") }
    var proteinText by remember { mutableStateOf(if (initialProtein > 0) initialProtein.toString() else "") }
    var carbsText by remember { mutableStateOf(if (initialCarbs > 0) initialCarbs.toString() else "") }
    var fatText by remember { mutableStateOf(if (initialFat > 0) initialFat.toString() else "") }
    var servingSize by remember { mutableStateOf(initialServingSize) }
    
    // Validation
    val isNameValid = name.isNotBlank()
    val isCaloriesValid = caloriesText.toIntOrNull()?.let { it > 0 } ?: false
    val canSave = isNameValid && isCaloriesValid
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditMode) "Edit Food" else "Add Custom Food",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
                
                // Name field (required)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name *") },
                    placeholder = { Text("e.g., Nasi Goreng Kampung") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = name.isNotEmpty() && !isNameValid
                )
                
                // Calories field (required)
                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = { caloriesText = it.filter { c -> c.isDigit() } },
                    label = { Text("Calories (kcal) *") },
                    placeholder = { Text("e.g., 350") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = caloriesText.isNotEmpty() && !isCaloriesValid
                )
                
                // Macros row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it.filter { c -> c.isDigit() } },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it.filter { c -> c.isDigit() } },
                        label = { Text("Carbs (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fatText,
                        onValueChange = { fatText = it.filter { c -> c.isDigit() } },
                        label = { Text("Fat (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                
                // Serving size
                OutlinedTextField(
                    value = servingSize,
                    onValueChange = { servingSize = it },
                    label = { Text("Serving Size") },
                    placeholder = { Text("e.g., 1 plate, 100g") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons
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
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSave(
                                name.trim(),
                                caloriesText.toIntOrNull() ?: 0,
                                proteinText.toIntOrNull() ?: 0,
                                carbsText.toIntOrNull() ?: 0,
                                fatText.toIntOrNull() ?: 0,
                                servingSize.ifBlank { "1 serving" }
                            )
                        },
                        enabled = canSave,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue500
                        )
                    ) {
                        Text(if (isEditMode) "Update" else "Save", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
