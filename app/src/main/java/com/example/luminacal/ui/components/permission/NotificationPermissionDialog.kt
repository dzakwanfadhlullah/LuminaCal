package com.example.luminacal.ui.components.permission

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.luminacal.ui.theme.Blue500
import com.example.luminacal.ui.theme.Green500
import com.example.luminacal.ui.theme.Pink500
import com.example.luminacal.util.NotificationPermissionHelper

/**
 * Notification Permission Request Dialog
 * Shows a friendly dialog explaining why notifications are needed
 */
@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        NotificationPermissionHelper.markAskedForPermission(context)
        onPermissionResult(isGranted)
        if (isGranted) {
            onDismiss()
        }
    }
    
    // Check if permission is already granted
    val isGranted = NotificationPermissionHelper.isNotificationPermissionGranted(context)
    
    if (isGranted) {
        onPermissionResult(true)
        onDismiss()
        return
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Blue500.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Blue500,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Title
                Text(
                    text = "Enable Notifications",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Description
                Text(
                    text = "Get timely meal reminders, daily summaries, and celebrate your achievements! We'll only notify you when it matters.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Benefits list
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BenefitRow("🍽️ Meal reminders at your preferred times")
                    BenefitRow("📊 Daily progress summaries")
                    BenefitRow("🔥 Streak achievements")
                    BenefitRow("🎯 Goal celebrations")
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            onPermissionResult(true)
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue500)
                ) {
                    Text(
                        "Enable Notifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextButton(
                    onClick = {
                        onPermissionResult(false)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Maybe Later",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BenefitRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}

/**
 * Permission Denied UI - shows button to open settings
 */
@Composable
fun NotificationPermissionDeniedCard(
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = Pink500.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsOff,
                contentDescription = null,
                tint = Pink500,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Notifications Disabled",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Pink500
                )
                Text(
                    text = "Enable in settings to receive meal reminders",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            IconButton(
                onClick = {
                    NotificationPermissionHelper.openNotificationSettings(context)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open Settings",
                    tint = Pink500
                )
            }
        }
    }
}

/**
 * Permission Status Indicator
 */
@Composable
fun NotificationPermissionStatus(
    isGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isGranted) Green500.copy(alpha = 0.1f) else Pink500.copy(alpha = 0.1f),
        animationSpec = spring(),
        label = "bgColor"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isGranted) Green500 else Pink500,
        animationSpec = spring(),
        label = "iconColor"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isGranted) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = if (isGranted) "Notifications enabled" else "Notifications disabled",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = iconColor,
                modifier = Modifier.weight(1f)
            )
            
            if (!isGranted) {
                TextButton(
                    onClick = onRequestPermission,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Enable",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Blue500
                    )
                }
            }
        }
    }
}
