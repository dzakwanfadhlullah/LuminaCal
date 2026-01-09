package com.example.luminacal.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Utility class for handling POST_NOTIFICATIONS permission on Android 13+
 */
object NotificationPermissionHelper {
    
    /**
     * Check if notification permission is granted
     * Returns true for Android 12 and below (no runtime permission needed)
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 12 and below, no runtime permission needed
            true
        }
    }
    
    /**
     * Check if we should show permission rationale
     */
    fun shouldShowRationale(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            false
        }
    }
    
    /**
     * Open app notification settings
     */
    fun openNotificationSettings(context: Context) {
        val intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.parse("package:${context.packageName}")
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    /**
     * Check if this is the first time asking for permission
     */
    fun isFirstTimeAsking(context: Context): Boolean {
        val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("first_time_asking", true)
    }
    
    /**
     * Mark that we've asked for permission
     */
    fun markAskedForPermission(context: Context) {
        val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("first_time_asking", false).apply()
    }
    
    /**
     * Get the permission string for Android 13+
     */
    fun getNotificationPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            ""
        }
    }
    
    /**
     * Check if device requires runtime permission for notifications
     */
    fun requiresRuntimePermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }
}
