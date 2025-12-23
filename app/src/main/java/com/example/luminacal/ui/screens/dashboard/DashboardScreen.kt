package com.example.luminacal.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.luminacal.model.CalorieState
import com.example.luminacal.model.HistoryEntry
import com.example.luminacal.model.Macros
import com.example.luminacal.model.MealType
import com.example.luminacal.ui.components.*
import com.example.luminacal.ui.theme.*

@Composable
fun DashboardScreen(
    calorieState: CalorieState,
    macros: Macros,
    history: List<HistoryEntry>,
    onLogClick: (HistoryEntry) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TODAY",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                AsyncImage(
                    model = "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix",
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Main Ring Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Status Badge
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Color(0xFF22C55E).copy(alpha = 0.1f))
                            .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.2f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "On Track",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF22C55E)
                        )
                    }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppleRing(
                        progress = calorieState.consumed.toFloat() / calorieState.target,
                        labelProvider = {
                            AnimatedNumber(
                                value = calorieState.target - calorieState.consumed,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        subLabel = "Remaining",
                        icon = Icons.Default.Whatshot,
                        color = Color(0xFFFFB88C)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Macros Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MacroProgressBar(
                            label = "Protein",
                            value = macros.protein,
                            max = 150,
                            color = Color(0xFF3B82F6),
                            modifier = Modifier.weight(1f)
                        )
                        MacroProgressBar(
                            label = "Carbs",
                            value = macros.carbs,
                            max = 200,
                            color = Color(0xFF22C55E),
                            modifier = Modifier.weight(1f)
                        )
                        MacroProgressBar(
                            label = "Fat",
                            value = macros.fat,
                            max = 70,
                            color = Color(0xFFFB923C),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                }
            }
        }

        // Timeline Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Logs",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelMedium,
                    color = Blue500,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Timeline Logs
        itemsIndexed(history) { index, entry ->
            Box(modifier = Modifier.fillMaxWidth()) {
                // Vertical Connector Line
                if (index < history.size - 1) {
                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 40.dp)
                            .width(2.dp)
                            .height(40.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Blue500.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )
                }

                GlassCard(
                    modifier = Modifier.padding(start = 8.dp).fillMaxWidth(),
                    onClick = { onLogClick(entry) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon Circle
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when (entry.type) {
                                        MealType.BREAKFAST -> Color(0xFFDBEAFE)
                                        MealType.LUNCH -> Color(0xFFDCFCE7)
                                        MealType.SNACK -> Color(0xFFFEE2E2)
                                        else -> Color(0xFFF3F4F6)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (entry.type) {
                                    MealType.BREAKFAST -> "🍳"
                                    MealType.LUNCH -> "🥗"
                                    MealType.SNACK -> "🍎"
                                    else -> "🍽️"
                                },
                                fontSize = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${entry.time} • ${entry.type.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = entry.calories.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "kcal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
