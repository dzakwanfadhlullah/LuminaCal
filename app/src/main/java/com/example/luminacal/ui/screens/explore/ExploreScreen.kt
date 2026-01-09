package com.example.luminacal.ui.screens.explore

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.res.stringResource
import com.example.luminacal.R
import com.example.luminacal.data.ml.FoodNutritionDatabase
import com.example.luminacal.data.local.CustomFoodEntity
import com.example.luminacal.ui.components.AddCustomFoodDialog
import com.example.luminacal.viewmodel.MainViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ExploreScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onFoodClick: (Recipe) -> Unit,
    onManualAdd: (String, Int, Macros, MealType) -> Unit = { _, _, _, _ -> },
    customFoods: List<CustomFoodEntity> = emptyList(),
    onSaveCustomFood: (String, Int, Int, Int, Int, String) -> Unit = { _, _, _, _, _, _ -> },
    onDeleteCustomFood: (Long) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var maxCaloriesFilter by remember { mutableStateOf<Int?>(null) }
    val categories = listOf("All", "My Foods", "Indonesian", "FastFood", "Drinks", "Snacks", "Breakfast")
    val calorieFilters = listOf("All" to null, "< 300" to 300, "< 500" to 500)
    var showManualEntry by remember { mutableStateOf(false) }
    var showAddCustomFood by remember { mutableStateOf(false) }
    
    // Convert NutritionInfo from database to Recipe for display
    val allRecipes = remember {
        FoodNutritionDatabase.getAllFoods().map { nutrition ->
            val category = when {
                nutrition.name.startsWith("KFC") || nutrition.name.startsWith("McD") || 
                nutrition.name.startsWith("BK") || nutrition.name.contains("Burger") -> "FastFood"
                nutrition.name.contains("Starbucks") || nutrition.name.contains("Chatime") ||
                nutrition.name.contains("Kenangan") || nutrition.name.contains("Kopi") ||
                nutrition.name.contains("Teh") || nutrition.name.contains("Es ") ||
                nutrition.name.contains("Jus") || nutrition.name.contains("Milk Tea") -> "Drinks"
                nutrition.name.contains("Gorengan") || nutrition.name.contains("Martabak") ||
                nutrition.name.contains("Pisang Goreng") || nutrition.name.contains("Cireng") ||
                nutrition.name.contains("Combro") || nutrition.name.contains("Kue") -> "Snacks"
                nutrition.name.contains("Bubur") || nutrition.name.contains("Toast") ||
                nutrition.name.contains("Omelet") || nutrition.name.contains("Pancakes") -> "Breakfast"
                else -> "Indonesian"
            }
            Recipe(
                name = nutrition.name,
                calories = "${nutrition.calories} kcal",
                time = "~",
                category = category,
                imageUrl = nutrition.imageUrl ?: "https://images.pexels.com/photos/1640777/pexels-photo-1640777.jpeg?w=800"
            )
        }
    }
    
    // Convert custom foods to Recipe format for unified display
    val customRecipes = remember(customFoods) {
        customFoods.map { food ->
            Recipe(
                name = food.name,
                calories = "${food.calories} kcal",
                time = food.servingSize,
                category = "My Foods",
                imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=800" // Default food image
            )
        }
    }

    val recipes = remember(selectedCategory, searchQuery, maxCaloriesFilter, allRecipes, customRecipes) {
        val maxCal = maxCaloriesFilter
        val allFoods = if (selectedCategory == "My Foods") {
            customRecipes
        } else {
            allRecipes + customRecipes
        }
        allFoods.filter { recipe ->
            val matchesCategory = selectedCategory == "All" || selectedCategory == "My Foods" || recipe.category == selectedCategory
            val matchesSearch = searchQuery.isEmpty() || 
                recipe.name.contains(searchQuery, ignoreCase = true)
            val recipeCalories = recipe.calories.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
            val matchesCalories = maxCal == null || recipeCalories < maxCal
            matchesCategory && matchesSearch && matchesCalories
        }.sortedBy { it.name }
    }
    
    // Show Add Custom Food Dialog
    if (showAddCustomFood) {
        AddCustomFoodDialog(
            onDismiss = { showAddCustomFood = false },
            onSave = { name, cals, protein, carbs, fat, serving ->
                onSaveCustomFood(name, cals, protein, carbs, fat, serving)
                showAddCustomFood = false
            }
        )
    }

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
                        Text(stringResource(R.string.explore_title), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { 
                                Text(
                                    stringResource(R.string.explore_search_hint),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                ) 
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search, 
                                    contentDescription = "Search foods", 
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear search",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Blue500,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
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
                                text = when (cat) {
                                    "All" -> stringResource(R.string.category_all)
                                    "Indonesian" -> stringResource(R.string.category_indonesian)
                                    "FastFood" -> "Fast Food"
                                    "Drinks" -> "Drinks"
                                    "Snacks" -> stringResource(R.string.category_snacks)
                                    "Breakfast" -> stringResource(R.string.category_breakfast)
                                    else -> cat
                                },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = if (isSelected) Color.White else Slate900,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Calorie Filter Chips
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(calorieFilters) { (label, maxCal) ->
                        val isSelected = maxCaloriesFilter == maxCal
                        Surface(
                            color = if (isSelected) Green500 else Color.White,
                            shape = RoundedCornerShape(12.dp),
                            border = if (!isSelected) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null,
                            modifier = Modifier
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    maxCaloriesFilter = maxCal
                                }
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = if (isSelected) Color.White else Slate900,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Empty Search Results
            if (recipes.isEmpty()) {
                item {
                    com.example.luminacal.ui.components.EmptyStateCard(
                        icon = Icons.Default.SearchOff,
                        title = stringResource(R.string.no_recipes_found),
                        subtitle = if (searchQuery.isNotEmpty()) 
                            stringResource(R.string.no_recipes_match, searchQuery)
                        else 
                            stringResource(R.string.no_recipes_category),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)
                    )
                }
            }

            // Grid Content
            itemsIndexed(recipes.chunked(2)) { rowIndex, rowRecipes ->
                val animatedAlpha by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = rowIndex * 100
                    ),
                    label = "fadeIn"
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .graphicsLayer(alpha = animatedAlpha, translationY = (1f - animatedAlpha) * 50f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowRecipes.forEach { recipe ->
                        val recipeCalories = recipe.calories.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                        RecipeCard(
                            recipe = recipe, 
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onFoodClick(recipe) 
                                },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onQuickAdd = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                // Look up real macros from database instead of estimating
                                val nutrition = FoodNutritionDatabase.lookup(recipe.name)
                                val protein = nutrition?.protein ?: (recipeCalories * 0.25 / 4).toInt()
                                val carbs = nutrition?.carbs ?: (recipeCalories * 0.50 / 4).toInt()
                                val fat = nutrition?.fat ?: (recipeCalories * 0.25 / 9).toInt()
                                val mealType = when (recipe.category) {
                                    "Breakfast" -> MealType.BREAKFAST
                                    "Snacks" -> MealType.SNACK
                                    "Dinner" -> MealType.DINNER
                                    else -> MealType.LUNCH
                                }
                                onManualAdd(recipe.name, recipeCalories, Macros(protein, carbs, fat), mealType)
                            }
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
        
        // FAB for adding custom foods
        FloatingActionButton(
            onClick = { showAddCustomFood = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 188.dp, end = 24.dp),
            containerColor = Peach400,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Custom Food")
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
        title = { Text(stringResource(R.string.manual_entry_title), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.food_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { if (it.all { char -> char.isDigit() }) calories = it },
                        label = { Text(stringResource(R.string.dashboard_kcal)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { if (it.all { char -> char.isDigit() }) protein = it },
                        label = { Text(stringResource(R.string.macro_protein)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { if (it.all { char -> char.isDigit() }) carbs = it },
                        label = { Text(stringResource(R.string.macro_carbs)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { if (it.all { char -> char.isDigit() }) fat = it },
                        label = { Text(stringResource(R.string.macro_fat)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Text(stringResource(R.string.meal_timing), style = MaterialTheme.typography.labelMedium, color = Blue500)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MealType.values().forEach { type ->
                        val isSelected = mealType == type
                        Surface(
                            color = if (isSelected) Blue500 else MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).clickable { mealType = type }
                        ) {
                            Text(
                                text = when (type) {
                                    MealType.BREAKFAST -> stringResource(R.string.meal_breakfast)
                                    MealType.LUNCH -> stringResource(R.string.meal_lunch)
                                    MealType.DINNER -> stringResource(R.string.meal_dinner)
                                    MealType.SNACK -> stringResource(R.string.meal_snack)
                                },
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
                Text(stringResource(R.string.add_to_log))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
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
    animatedVisibilityScope: AnimatedVisibilityScope,
    onQuickAdd: () -> Unit = {}
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
                        .background(SurfaceVariantLight.copy(alpha = 0.5f))
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
                        IconButton(
                            onClick = onQuickAdd,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                Icons.Default.Add, 
                                contentDescription = "Quick add", 
                                tint = Blue500, 
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
