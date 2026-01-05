package com.example.luminacal.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Utility class for persisting app preferences using SharedPreferences.
 * Handles settings like dark mode, notification preferences, etc.
 */
class AppPreferences(context: Context) {
    
    companion object {
        private const val PREFS_NAME = "luminacal_prefs"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        
        @Volatile
        private var instance: AppPreferences? = null
        
        fun getInstance(context: Context): AppPreferences {
            return instance ?: synchronized(this) {
                instance ?: AppPreferences(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    /**
     * Dark mode preference - persists across app restarts
     */
    var darkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit { putBoolean(KEY_DARK_MODE, value) }
    
    /**
     * Notifications enabled preference
     */
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_NOTIFICATIONS_ENABLED, value) }
}
