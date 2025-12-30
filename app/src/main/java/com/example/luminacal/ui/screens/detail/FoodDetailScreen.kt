package com.example.luminacal.ui.screens.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.luminacal.ui.components.GlassButton
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.model.Macros
import com.example.luminacal.model.MealType
import androidx.compose.ui.res.stringResource
import com.example.luminacal.R
import com.example.luminacal.ui.theme.*

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FoodDetailScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    foodName: String,
    calories: String,
    time: String,
    category: String,
    imageUrl: String,
    onBack: () -> Unit,
    onLogMeal: (String, Int, Macros, MealType) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var portionSize by remember { mutableStateOf(100f) }
    
    // Parse base calories from string like "450 kcal"
    val baseCalories = calories.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 250
    val currentCalories = (baseCalories * (portionSize / 100)).toInt()
    
    // Estimate macros based on category
    val estimatedMacros = when (category.lowercase()) {
        "vegan" -> Macros(protein = 12, carbs = 45, fat = 8)
        "dinner" -> Macros(protein = 35, carbs = 20, fat = 22)
        "breakfast" -> Macros(protein = 15, carbs = 50, fat = 12)
        "indonesian" -> Macros(protein = 20, carbs = 55, fat = 18)
        "snacks" -> Macros(protein = 5, carbs = 30, fat = 10)
        else -> Macros(protein = 20, carbs = 40, fat = 15)
    }
    
    // Determine meal type
    val mealType = when (category.lowercase()) {
        "breakfast" -> MealType.BREAKFAST
        "dinner" -> MealType.DINNER
        "snacks" -> MealType.SNACK
        else -> MealType.LUNCH
    }

    with(sharedTransitionScope) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Hero Image
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = foodName,
                            modifier = Modifier
                                .fillMaxSize()
                                .sharedElement(
                                    rememberSharedContentState(key = "food_image_$foodName"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                ),
                            contentScale = ContentScale.Crop
                        )
                    
                        // Back Button
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.TopStart)
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = stringResource(R.string.cd_back_button), tint = Color.White)
                        }
                        
                        // Time Badge
                        Surface(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                        ) {
                            Text(
                                text = time,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(24.dp)) {
                        // Category Badge
                        Surface(
                            color = Blue500.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = category.uppercase(),
                                color = Blue500,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = foodName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Calories Ring/Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "$currentCalories",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = stringResource(R.string.total_calories),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Macros
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MacroBadge(stringResource(R.string.macro_protein), "${estimatedMacros.protein}g", Blue500, Modifier.weight(1f))
                            MacroBadge(stringResource(R.string.macro_carbs), "${estimatedMacros.carbs}g", Green500, Modifier.weight(1f))
                            MacroBadge(stringResource(R.string.macro_fat), "${estimatedMacros.fat}g", Peach400, Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Portion Slider
                        Text(
                            text = stringResource(R.string.portion_size),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${portionSize.toInt()}g", color = Blue400, fontWeight = FontWeight.Bold)
                            Slider(
                                value = portionSize,
                                onValueChange = { portionSize = it },
                                valueRange = 50f..500f,
                                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                                colors = SliderDefaults.colors(
                                    thumbColor = Blue500,
                                    activeTrackColor = Blue500,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }

            // Floating Add Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.BottomCenter)
            ) {
                GlassButton(
                    text = stringResource(R.string.log_this_meal),
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLogMeal(foodName, currentCalories, estimatedMacros, mealType)
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun MacroBadge(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = value, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 10.sp)
        }
    }
}
