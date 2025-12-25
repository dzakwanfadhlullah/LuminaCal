package com.example.luminacal.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.runtime.remember
import androidx.compose.foundation.clickable
import com.example.luminacal.ui.components.GlassCard
import com.example.luminacal.ui.theme.Blue500
import com.example.luminacal.ui.theme.Peach400
import com.example.luminacal.ui.theme.Pink500

@Composable
fun ProfileScreen(
    darkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onHealthClick: () -> Unit = {},
    onExportCSV: () -> Unit = {},
    onExportJSON: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Top Tools
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
        }

        // Animated Profile Image
        item {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, androidx.compose.ui.graphics.Brush.linearGradient(listOf(Blue500, Pink500))),
                    color = Color.Transparent
                ) {
                    AsyncImage(
                        model = "https://api.dicebear.com/7.x/avataaars/svg?seed=Alex",
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            // Action to edit profile
                        },
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit Profile", 
                        tint = Peach400, 
                        modifier = Modifier.padding(8.dp).size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Alex Morgan", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Premium Member", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 14.sp)
            }
        }

        // Main Groups
        item {
            GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                ProfileItemRow(
                    icon = Icons.Default.MonitorHeart, 
                    title = "Health & Goals", 
                    trailing = "TDEE Calculator",
                    tint = Color(0xFFEF4444),
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onHealthClick()
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                ProfileItemRow(
                    icon = Icons.Default.TrackChanges, 
                    title = "Goals", 
                    tint = Color(0xFFFB923C),
                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                ProfileItemRow(
                    icon = Icons.Default.Devices, 
                    title = "Devices & Apps", 
                    trailing = "2 Connected", 
                    tint = Color(0xFF22C55E),
                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
                )
            }
        }

        // Data Management
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "DATA MANAGEMENT", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                    ProfileItemRow(
                        icon = Icons.Default.FileDownload, 
                        title = "Export Meals (CSV)", 
                        tint = Color(0xFF3B82F6),
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onExportCSV()
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                    ProfileItemRow(
                        icon = Icons.Default.Backup, 
                        title = "Full Backup (JSON)", 
                        tint = Color(0xFF8B5CF6),
                        onClick = { 
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onExportJSON()
                        }
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "PREFERENCES", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                GlassCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
                    ProfileItemRow(Icons.Default.Person, "Dietary Needs", tint = Color(0xFFA855F7))
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF64748B).copy(alpha = 0.1f)
                            ) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.padding(6.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Dark Mode", fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = darkMode, 
                            onCheckedChange = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onToggleDarkMode() 
                            }, 
                            colors = SwitchDefaults.colors(checkedThumbColor = Blue500)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                    // Notification Setting Placeholder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.size(32.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF3B82F6).copy(alpha = 0.1f)
                            ) {
                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.padding(6.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Meal Reminders", fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = true, 
                            onCheckedChange = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }, 
                            colors = SwitchDefaults.colors(checkedThumbColor = Blue500)
                        )
                    }
                }
            }
        }

        // Sign Out
        item {
            Text(
                "Sign Out", 
                color = Color.Red.copy(alpha = 0.7f), 
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .clickable { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
            )
            Text(
                "Version 1.0.0 (Build 204)", 
                fontSize = 10.sp, 
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileItemRow(
    icon: ImageVector, 
    title: String, 
    trailing: String? = null, 
    tint: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(8.dp),
                color = tint.copy(alpha = 0.1f)
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(6.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (trailing != null) {
                Text(trailing, fontSize = 12.sp, color = Color(0xFF22C55E), modifier = Modifier.padding(end = 8.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String) {
    GlassCard(modifier = Modifier.fillMaxWidth(), onClick = {}) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(title, color = MaterialTheme.colorScheme.onSurface)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        }
    }
}
