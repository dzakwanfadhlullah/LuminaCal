package com.example.luminacal.ui.screens.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.ui.theme.Blue500
import com.example.luminacal.ui.theme.Slate800
import com.example.luminacal.ui.theme.Slate900

@Composable
fun ExploreScreen() {
    val categories = listOf("All", "Breakfast", "Vegan", "Keto", "Snacks")
    val recipes = listOf(
        Recipe("Avocado Toast Deluxe", "340 kcal", "10m", "Breakfast", "🥑"),
        Recipe("Grilled Chicken Pesto", "550 kcal", "35m", "High Protein", "🍗"),
        Recipe("Quinoa Power Salad", "420 kcal", "20m", "Vegan", "🥗"),
        Recipe("Zucchini Noodles", "180 kcal", "15m", "Keto", "🥒"),
        Recipe("Mixed Berries", "90 kcal", "5m", "Snack", "🫐"),
        Recipe("Tropical Mango", "120 kcal", "1h", "Fruit", "🥭")
    )

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
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isAll = cat == "All"
                    Surface(
                        color = if (isAll) Slate900 else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = if (!isAll) BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (isAll) Color.White else Slate900,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Grid Content
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                recipes.chunked(2).forEach { rowRecipes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowRecipes.forEach { recipe ->
                            RecipeCard(recipe = recipe, modifier = Modifier.weight(1f))
                        }
                        if (rowRecipes.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

data class Recipe(val name: String, val calories: String, val time: String, val category: String, val emoji: String)

@Composable
fun RecipeCard(recipe: Recipe, modifier: Modifier = Modifier) {
    GlassCard(
        modifier = modifier.height(200.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE2E8F0).copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text(recipe.emoji, fontSize = 48.sp)
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
