package com.example.luminacal.ui.components.charts

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.model.DailyCalories
import com.example.luminacal.model.WeightPoint
import com.example.luminacal.ui.theme.*

/**
 * Weekly Calorie Bar Chart
 */
@Composable
fun WeeklyCalorieChart(
    data: List<DailyCalories>,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animateProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "barAnimation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val barWidth = 24.dp.toPx()
            val spacing = (width - (barWidth * data.size)) / (data.size + 1)
            val maxCal = 3000f // Scaling factor

            // Draw target line
            if (data.isNotEmpty()) {
                val target = data.first().target
                val targetY = height - (target / maxCal * height)
                drawLine(
                    color = Color.White.copy(alpha = 0.2f),
                    start = Offset(0f, targetY),
                    end = Offset(width, targetY),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }

            data.forEachIndexed { index, daily ->
                val x = spacing + index * (barWidth + spacing)
                val barHeight = (daily.calories / maxCal * height) * animateProgress
                val y = height - barHeight
                
                // Bar shadow/glow
                drawRoundRect(
                    color = Blue500.copy(alpha = 0.1f),
                    topLeft = Offset(x, y - 4.dp.toPx()),
                    size = Size(barWidth, barHeight + 4.dp.toPx()),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )

                // Actual bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = if (daily.calories > daily.target) 
                            listOf(Pink500, Peach400) 
                        else 
                            listOf(Blue500, Color(0xFF818CF8))
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8.dp.toPx())
                )
            }
        }
    }
}

/**
 * Smooth Line Chart for Weight
 */
@Composable
fun WeightProgressChart(
    data: List<WeightPoint>,
    modifier: Modifier = Modifier
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animateProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1500, easing = EaseInOutQuart),
        label = "lineAnimation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val minWeight = data.minOf { it.weight } - 2
        val maxWeight = data.maxOf { it.weight } + 2
        val weightRange = maxWeight - minWeight
        
        val stepX = width / (data.size - 1)
        
        val points = data.mapIndexed { index, point ->
            Offset(
                x = index * stepX,
                y = height - ((point.weight - minWeight) / weightRange * height)
            )
        }

        val path = Path().apply {
            moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                val current = points[i]
                val prev = points[i - 1]
                val conX1 = prev.x + (current.x - prev.x) / 2f
                cubicTo(
                    conX1, prev.y,
                    conX1, current.y,
                    current.x, current.y
                )
            }
        }

        // Animated clipping
        clipRect(right = width * animateProgress) {
            // Draw gradient area
            val fillPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, height)
                lineTo(points.first().x, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Peach400.copy(alpha = 0.3f), Color.Transparent)
                )
            )

            // Draw line
            drawPath(
                path = path,
                color = Peach400,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Donut Chart for Macro Distribution
 */
@Composable
fun MacroDistributionChart(
    protein: Int,
    carbs: Int,
    fat: Int,
    modifier: Modifier = Modifier
) {
    val total = (protein + carbs + fat).toFloat()
    if (total == 0f) return

    val proteinAngle = (protein / total) * 360f
    val carbsAngle = (carbs / total) * 360f
    val fatAngle = (fat / total) * 360f

    var animationPlayed by remember { mutableStateOf(false) }
    val animateProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "donutAnimation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 20.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Protein
            drawArc(
                color = Blue500,
                startAngle = -90f,
                sweepAngle = proteinAngle * animateProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Carbs
            drawArc(
                color = Green500,
                startAngle = -90f + proteinAngle,
                sweepAngle = carbsAngle * animateProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Fat
            drawArc(
                color = Peach400,
                startAngle = -90f + proteinAngle + carbsAngle,
                sweepAngle = fatAngle * animateProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${total.toInt()}g",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Total macros",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
