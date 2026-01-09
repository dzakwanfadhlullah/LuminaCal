package com.example.luminacal.ui.screens.settings

import android.app.TimePickerDialog
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminacal.R
import com.example.luminacal.model.ReminderSettings
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.ui.components.input.GlassSwitch
import com.example.luminacal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(
    reminderSettings: ReminderSettings,
    onSettingsChange: (ReminderSettings) -> Unit,
    onBack: () -> Unit,
    onSave: (ReminderSettings) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    
    // Local state for editing
    var settings by remember { mutableStateOf(reminderSettings) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(R.string.reminder_settings_title),
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Meal Reminders Section
            item {
                Text(
                    text = "Meal Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Breakfast
            item {
                ReminderCard(
                    icon = Icons.Default.WbSunny,
                    iconColor = Peach400,
                    title = stringResource(R.string.reminder_breakfast),
                    enabled = settings.breakfastEnabled,
                    onEnabledChange = { settings = settings.copy(breakfastEnabled = it) },
                    hour = settings.breakfastHour,
                    minute = settings.breakfastMinute,
                    onTimeChange = { h, m -> 
                        settings = settings.copy(breakfastHour = h, breakfastMinute = m) 
                    }
                )
            }
            
            // Lunch
            item {
                ReminderCard(
                    icon = Icons.Default.LightMode,
                    iconColor = Blue500,
                    title = stringResource(R.string.reminder_lunch),
                    enabled = settings.lunchEnabled,
                    onEnabledChange = { settings = settings.copy(lunchEnabled = it) },
                    hour = settings.lunchHour,
                    minute = settings.lunchMinute,
                    onTimeChange = { h, m -> 
                        settings = settings.copy(lunchHour = h, lunchMinute = m) 
                    }
                )
            }
            
            // Dinner
            item {
                ReminderCard(
                    icon = Icons.Default.DarkMode,
                    iconColor = Purple500,
                    title = stringResource(R.string.reminder_dinner),
                    enabled = settings.dinnerEnabled,
                    onEnabledChange = { settings = settings.copy(dinnerEnabled = it) },
                    hour = settings.dinnerHour,
                    minute = settings.dinnerMinute,
                    onTimeChange = { h, m -> 
                        settings = settings.copy(dinnerHour = h, dinnerMinute = m) 
                    }
                )
            }
            
            // Other Reminders Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Other Reminders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Forgot to Log
            item {
                ReminderCard(
                    icon = Icons.Default.NotificationsActive,
                    iconColor = Pink500,
                    title = stringResource(R.string.reminder_forgot_to_log),
                    subtitle = "Reminder if no meals logged by evening",
                    enabled = settings.forgotToLogEnabled,
                    onEnabledChange = { settings = settings.copy(forgotToLogEnabled = it) },
                    hour = settings.forgotToLogHour,
                    minute = settings.forgotToLogMinute,
                    onTimeChange = { h, m -> 
                        settings = settings.copy(forgotToLogHour = h, forgotToLogMinute = m) 
                    }
                )
            }
            
            // Daily Summary
            item {
                ReminderCard(
                    icon = Icons.Default.Summarize,
                    iconColor = Green500,
                    title = stringResource(R.string.reminder_daily_summary),
                    subtitle = "End-of-day nutrition summary",
                    enabled = settings.dailySummaryEnabled,
                    onEnabledChange = { settings = settings.copy(dailySummaryEnabled = it) },
                    hour = settings.dailySummaryHour,
                    minute = settings.dailySummaryMinute,
                    onTimeChange = { h, m -> 
                        settings = settings.copy(dailySummaryHour = h, dailySummaryMinute = m) 
                    }
                )
            }
            
            // Active Days
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.reminder_days),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            item {
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val days = listOf("S", "M", "T", "W", "T", "F", "S")
                        days.forEachIndexed { index, day ->
                            val isSelected = settings.enabledDays.contains(index)
                            DayChip(
                                day = day,
                                isSelected = isSelected,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    val newDays = if (isSelected) {
                                        settings.enabledDays - index
                                    } else {
                                        settings.enabledDays + index
                                    }
                                    settings = settings.copy(enabledDays = newDays)
                                }
                            )
                        }
                    }
                }
            }
            
            // Save Button
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSave(settings)
                        onBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.reminder_save),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ReminderCard(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String? = null,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    GlassCard {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Title & Subtitle
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
                
                // Toggle
                Switch(
                    checked = enabled,
                    onCheckedChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onEnabledChange(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Blue500
                    )
                )
            }
            
            // Time Picker (only show if enabled)
            if (enabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            TimePickerDialog(
                                context,
                                { _, h, m -> onTimeChange(h, m) },
                                hour,
                                minute,
                                true
                            ).show()
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Blue500,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reminder Time", fontSize = 14.sp)
                    }
                    Text(
                        text = String.format("%02d:%02d", hour, minute),
                        fontWeight = FontWeight.Bold,
                        color = Blue500,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DayChip(
    day: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Blue500 else Color.Transparent,
        animationSpec = spring(),
        label = "dayBg"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        animationSpec = spring(),
        label = "dayText"
    )
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = if (isSelected) Blue500 else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 14.sp
        )
    }
}
