package com.example.luminacal.ui.screens.explore

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import coil.compose.AsyncImage
import com.example.luminacal.model.Macros
import com.example.luminacal.model.MealType
import com.example.luminacal.ui.components.GlassButton
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.ui.theme.*
import com.example.luminacal.viewmodel.MainViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExploreScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFoodClick: (Recipe) -> Unit,
    onManualAdd: (String, Int, Macros, MealType) -> Unit = { _, _, _, _ -> }
) {
    val haptic = LocalHapticFeedback.current
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Breakfast", "Indonesian", "Vegan", "Dinner", "Snacks")
    var showManualEntry by remember { mutableStateOf(false) }
    
    val allRecipes = remember {
        listOf(
            // --- BREAKFAST ---
            Recipe("Nasi Uduk Komplit", "480 kcal", "20m", "Breakfast", "https://images.pexels.com/photos/5638527/pexels-photo-5638527.jpeg?w=800"),
            Recipe("Bubur Ayam", "350 kcal", "15m", "Breakfast", "https://images.pexels.com/photos/6646351/pexels-photo-6646351.jpeg?w=800"),
            Recipe("Avocado Toast", "340 kcal", "10m", "Breakfast", "https://images.pexels.com/photos/1656666/pexels-photo-1656666.jpeg?w=800"),
            Recipe("Egg & Spinach Omelet", "220 kcal", "8m", "Breakfast", "https://images.pexels.com/photos/3026805/pexels-photo-3026805.jpeg?w=800"),
            Recipe("Blueberry Pancakes", "410 kcal", "15m", "Breakfast", "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?w=800"),
            Recipe("Nasi Kuning", "450 kcal", "15m", "Breakfast", "https://images.pexels.com/photos/5638527/pexels-photo-5638527.jpeg?w=800"),
            
            // --- INDONESIAN SPECIALTIES ---
            Recipe("Nasi Goreng", "580 kcal", "20m", "Indonesian", "https://images.pexels.com/photos/6646069/pexels-photo-6646069.jpeg?w=800"),
            Recipe("Sate Ayam", "420 kcal", "25m", "Indonesian", "https://images.pexels.com/photos/2673353/pexels-photo-2673353.jpeg?w=800"),
            Recipe("Rendang Sapi", "350 kcal", "3h", "Indonesian", "https://images.pexels.com/photos/5409015/pexels-photo-5409015.jpeg?w=800"),
            Recipe("Bakso Sapi", "480 kcal", "15m", "Indonesian", "https://images.pexels.com/photos/6646354/pexels-photo-6646354.jpeg?w=800"),
            Recipe("Soto Ayam", "280 kcal", "30m", "Indonesian", "https://images.pexels.com/photos/6646350/pexels-photo-6646350.jpeg?w=800"),
            Recipe("Gado-Gado", "310 kcal", "20m", "Indonesian", "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"),
            Recipe("Ayam Geprek", "620 kcal", "15m", "Indonesian", "https://images.pexels.com/photos/2338407/pexels-photo-2338407.jpeg?w=800"),
            Recipe("Ikan Bakar", "340 kcal", "30m", "Indonesian", "https://images.pexels.com/photos/3296279/pexels-photo-3296279.jpeg?w=800"),
            Recipe("Mie Goreng", "510 kcal", "15m", "Indonesian", "https://images.pexels.com/photos/6646071/pexels-photo-6646071.jpeg?w=800"),
            Recipe("Opor Ayam", "420 kcal", "40m", "Indonesian", "https://images.pexels.com/photos/2338407/pexels-photo-2338407.jpeg?w=800"),
            Recipe("Sop Buntut", "550 kcal", "50m", "Indonesian", "https://images.pexels.com/photos/2116094/pexels-photo-2116094.jpeg?w=800"),
            Recipe("Lontong Sayur", "470 kcal", "30m", "Indonesian", "https://images.pexels.com/photos/5638527/pexels-photo-5638527.jpeg?w=800"),
            Recipe("Rawon", "390 kcal", "2h", "Indonesian", "https://images.pexels.com/photos/2116094/pexels-photo-2116094.jpeg?w=800"),
            Recipe("Pempek", "380 kcal", "15m", "Indonesian", "https://images.pexels.com/photos/6646354/pexels-photo-6646354.jpeg?w=800"),
            Recipe("Ketoprak", "410 kcal", "15m", "Indonesian", "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"),
            Recipe("Ayam Bakar", "350 kcal", "45m", "Indonesian", "https://images.pexels.com/photos/2338407/pexels-photo-2338407.jpeg?w=800"),
            Recipe("Tempe Goreng", "180 kcal", "10m", "Indonesian", "https://images.pexels.com/photos/6646067/pexels-photo-6646067.jpeg?w=800"),
            Recipe("Sayur Asem", "95 kcal", "25m", "Indonesian", "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"),
            Recipe("Sate Padang", "480 kcal", "30m", "Indonesian", "https://images.pexels.com/photos/2673353/pexels-photo-2673353.jpeg?w=800"),
            Recipe("Gudeg", "450 kcal", "3h", "Indonesian", "https://images.pexels.com/photos/5638527/pexels-photo-5638527.jpeg?w=800"),
            Recipe("Siomay", "350 kcal", "15m", "Indonesian", "https://images.pexels.com/photos/6646354/pexels-photo-6646354.jpeg?w=800"),
            Recipe("Batagor", "430 kcal", "15m", "Indonesian", "https://images.pexels.com/photos/6646354/pexels-photo-6646354.jpeg?w=800"),
            
            // --- VEGAN & HEALTHY ---
            Recipe("Quinoa Buddha Bowl", "420 kcal", "20m", "Vegan", "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"),
            Recipe("Zucchini Pasta", "180 kcal", "15m", "Vegan", "https://images.pexels.com/photos/1279330/pexels-photo-1279330.jpeg?w=800"),
            Recipe("Roasted Sweet Potato", "160 kcal", "40m", "Vegan", "https://images.pexels.com/photos/5966434/pexels-photo-5966434.jpeg?w=800"),
            Recipe("Tofu Salad", "170 kcal", "10m", "Vegan", "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"),
            Recipe("Tempeh Buddha Bowl", "310 kcal", "20m", "Vegan", "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"),
            Recipe("Hummus Wrap", "280 kcal", "12m", "Vegan", "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"),
            Recipe("Lentil Soup", "220 kcal", "35m", "Vegan", "https://images.pexels.com/photos/2116094/pexels-photo-2116094.jpeg?w=800"),
            
            // --- DINNER & PROTEIN ---
            Recipe("Grilled Chicken", "450 kcal", "30m", "Dinner", "https://images.pexels.com/photos/2338407/pexels-photo-2338407.jpeg?w=800"),
            Recipe("Beef Stir Fry", "520 kcal", "20m", "Dinner", "https://images.pexels.com/photos/3535383/pexels-photo-3535383.jpeg?w=800"),
            Recipe("Salmon Fillet", "380 kcal", "20m", "Dinner", "https://images.pexels.com/photos/3296279/pexels-photo-3296279.jpeg?w=800"),
            Recipe("Ribeye Steak", "650 kcal", "25m", "Dinner", "https://images.pexels.com/photos/1251198/pexels-photo-1251198.jpeg?w=800"),
            Recipe("Shrimp Scampi", "320 kcal", "15m", "Dinner", "https://images.pexels.com/photos/3298637/pexels-photo-3298637.jpeg?w=800"),
            Recipe("Turkey Breast", "290 kcal", "45m", "Dinner", "https://images.pexels.com/photos/6210959/pexels-photo-6210959.jpeg?w=800"),
            Recipe("Lamb Chops", "580 kcal", "30m", "Dinner", "https://images.pexels.com/photos/3535383/pexels-photo-3535383.jpeg?w=800"),
            
            // --- SNACKS & DRINKS ---
            Recipe("Greek Yogurt", "210 kcal", "5m", "Snacks", "https://images.pexels.com/photos/1775043/pexels-photo-1775043.jpeg?w=800"),
            Recipe("Mixed Berries", "80 kcal", "5m", "Snacks", "https://images.pexels.com/photos/1132047/pexels-photo-1132047.jpeg?w=800"),
            Recipe("Dark Chocolate", "180 kcal", "10m", "Snacks", "https://images.pexels.com/photos/65882/chocolate-dark-coffee-confiserie-65882.jpeg?w=800"),
            Recipe("Pisang Goreng", "240 kcal", "15m", "Snacks", "https://images.pexels.com/photos/6646067/pexels-photo-6646067.jpeg?w=800"),
            Recipe("Martabak Manis", "320 kcal", "10m", "Snacks", "https://images.pexels.com/photos/376464/pexels-photo-376464.jpeg?w=800"),
            Recipe("Alpukat Kocok", "350 kcal", "5m", "Snacks", "https://images.pexels.com/photos/1656666/pexels-photo-1656666.jpeg?w=800"),
            Recipe("Fresh Orange Juice", "120 kcal", "5m", "Snacks", "https://images.pexels.com/photos/158053/fresh-orange-juice-squeezed-refreshing-citrus-158053.jpeg?w=800"),
            Recipe("Iced Matcha Latte", "150 kcal", "5m", "Snacks", "https://images.pexels.com/photos/5946651/pexels-photo-5946651.jpeg?w=800"),
            Recipe("Boba Milk Tea", "420 kcal", "5m", "Snacks", "https://images.pexels.com/photos/4053293/pexels-photo-4053293.jpeg?w=800"),
            Recipe("Banana Smoothie", "210 kcal", "5m", "Snacks", "https://images.pexels.com/photos/3679973/pexels-photo-3679973.jpeg?w=800"),
            Recipe("Edamame", "120 kcal", "10m", "Snacks", "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"),
            Recipe("Chia Pudding", "160 kcal", "5m", "Snacks", "https://images.pexels.com/photos/1775043/pexels-photo-1775043.jpeg?w=800"),
            Recipe("Roasted Almonds", "170 kcal", "5m", "Snacks", "https://images.pexels.com/photos/1295572/pexels-photo-1295572.jpeg?w=800"),
            Recipe("Fruit Salad", "130 kcal", "10m", "Snacks", "https://images.pexels.com/photos/1132047/pexels-photo-1132047.jpeg?w=800"),
            Recipe("Es Campur", "280 kcal", "10m", "Snacks", "https://images.pexels.com/photos/1132047/pexels-photo-1132047.jpeg?w=800")
        )
    }

    val recipes = allRecipes.filter { it.category == selectedCategory || selectedCategory == "All" }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Search Header
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Column {
                        Text("Explore", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Search recipes, ingredients...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                            }
                        }
                    }
                }
            }

            // Category Filter
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(categories) { cat ->
                        val isSelected = cat == selectedCategory
                        Surface(
                            color = if (isSelected) Slate900 else Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = if (!isSelected) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null,
                            modifier = Modifier
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedCategory = cat
                                }
                        ) {
                            Text(
                                text = cat,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = if (isSelected) Color.White else Slate900,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Grid Content
            items(recipes.chunked(2)) { rowRecipes ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowRecipes.forEach { recipe ->
                        RecipeCard(
                            recipe = recipe, 
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onFoodClick(recipe) 
                                },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                    if (rowRecipes.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Floating Action Button for Manual Entry
        FloatingActionButton(
            onClick = { showManualEntry = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 120.dp, end = 24.dp),
            containerColor = Blue500,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Manual Entry")
        }
    }

    if (showManualEntry) {
        ManualEntryDialog(
            onDismiss = { showManualEntry = false },
            onConfirm = { name, calories, protein, carbs, fat, type ->
                onManualAdd(name, calories, Macros(protein, carbs, fat), type)
                showManualEntry = false
            }
        )
    }
}

@Composable
fun ManualEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Int, Int, Int, MealType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf(MealType.LUNCH) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom Food Entry", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                        label = { Text("Kcal") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { if (it.all { char -> char.isDigit() }) protein = it },
                        label = { Text("Protein") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { if (it.all { char -> char.isDigit() }) carbs = it },
                        label = { Text("Carbs") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { if (it.all { char -> char.isDigit() }) fat = it },
                        label = { Text("Fat") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Text("Meal Timing", style = MaterialTheme.typography.labelMedium, color = Blue500)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MealType.values().forEach { type ->
                        val isSelected = mealType == type
                        Surface(
                            color = if (isSelected) Blue500 else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).clickable { mealType = type }
                        ) {
                            Text(
                                text = type.name.lowercase().capitalize(),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (name.isNotBlank() && calories.isNotBlank()) {
                        onConfirm(
                            name, 
                            calories.toIntOrNull() ?: 0, 
                            protein.toIntOrNull() ?: 0,
                            carbs.toIntOrNull() ?: 0,
                            fat.toIntOrNull() ?: 0,
                            mealType
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Blue500),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add to Log")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

data class Recipe(val name: String, val calories: String, val time: String, val category: String, val imageUrl: String)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RecipeCard(
    recipe: Recipe, 
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        GlassCard(
            modifier = modifier.height(240.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFFE2E8F0).copy(alpha = 0.5f))
                ) {
                    AsyncImage(
                        model = recipe.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .sharedElement(
                                rememberSharedContentState(key = "food_image_${recipe.name}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                        contentScale = ContentScale.Crop
                    )
                    
                    Surface(
                        color = Slate900.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                    ) {
                        Text(
                            recipe.time, 
                            color = Color.White, 
                            fontSize = 10.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(recipe.category.uppercase(), fontSize = 10.sp, color = Blue500, fontWeight = FontWeight.Bold)
                    Text(recipe.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(recipe.calories, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Icon(Icons.Default.Add, contentDescription = null, tint = Blue500.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
