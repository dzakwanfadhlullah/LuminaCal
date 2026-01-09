package com.example.luminacal.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Secure preferences utility using EncryptedSharedPreferences.
 * 
 * This class provides encrypted storage for sensitive user data like:
 * - Health metrics preferences
 * - User settings that contain personal information
 * - Any data that should be protected from unauthorized access
 * 
 * Falls back to regular SharedPreferences if encryption fails (e.g., on older devices).
 */
class EncryptedPreferences(context: Context) {
    
    companion object {
        private const val TAG = "EncryptedPreferences"
        private const val ENCRYPTED_PREFS_NAME = "luminacal_secure_prefs"
        
        // Keys for encrypted preferences
        const val KEY_USER_NAME = "user_name"
        const val KEY_LAST_WEIGHT = "last_weight"
        const val KEY_HEIGHT = "height"
        const val KEY_AGE = "age"
        const val KEY_GENDER = "gender"
        const val KEY_TARGET_WEIGHT = "target_weight"
        const val KEY_WATER_TARGET_ML = "water_target_ml"
        
        @Volatile
        private var instance: EncryptedPreferences? = null
        
        fun getInstance(context: Context): EncryptedPreferences {
            return instance ?: synchronized(this) {
                instance ?: EncryptedPreferences(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private val prefs: SharedPreferences = try {
        // Create or get the master key for encryption
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        // Create encrypted shared preferences
        EncryptedSharedPreferences.create(
            context,
            ENCRYPTED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to regular SharedPreferences if encryption fails
        // This can happen on some devices or if there's a keystore issue
        Log.e(TAG, "Failed to create encrypted preferences, falling back to regular prefs", e)
        context.getSharedPreferences(ENCRYPTED_PREFS_NAME + "_fallback", Context.MODE_PRIVATE)
    }
    
    // =====================
    // String preferences
    // =====================
    
    fun getString(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }
    
    fun setString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }
    
    // =====================
    // Float preferences
    // =====================
    
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return prefs.getFloat(key, defaultValue)
    }
    
    fun setFloat(key: String, value: Float) {
        prefs.edit { putFloat(key, value) }
    }
    
    // =====================
    // Int preferences
    // =====================
    
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }
    
    fun setInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }
    
    // =====================
    // Boolean preferences
    // =====================
    
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }
    
    fun setBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }
    
    // =====================
    // Convenience properties for common health data
    // =====================
    
    var userName: String
        get() = getString(KEY_USER_NAME, "User")
        set(value) = setString(KEY_USER_NAME, value)
    
    var lastWeight: Float
        get() = getFloat(KEY_LAST_WEIGHT, 70f)
        set(value) = setFloat(KEY_LAST_WEIGHT, value)
    
    var height: Float
        get() = getFloat(KEY_HEIGHT, 170f)
        set(value) = setFloat(KEY_HEIGHT, value)
    
    var age: Int
        get() = getInt(KEY_AGE, 25)
        set(value) = setInt(KEY_AGE, value)
    
    var gender: String
        get() = getString(KEY_GENDER, "MALE")
        set(value) = setString(KEY_GENDER, value)
    
    var targetWeight: Float
        get() = getFloat(KEY_TARGET_WEIGHT, 65f)
        set(value) = setFloat(KEY_TARGET_WEIGHT, value)
    
    var waterTargetMl: Int
        get() = getInt(KEY_WATER_TARGET_ML, 2000)
        set(value) = setInt(KEY_WATER_TARGET_ML, value)
    
    // =====================
    // Utility functions
    // =====================
    
    /**
     * Check if a key exists in preferences
     */
    fun contains(key: String): Boolean = prefs.contains(key)
    
    /**
     * Remove a specific key
     */
    fun remove(key: String) {
        prefs.edit { remove(key) }
    }
    
    /**
     * Clear all encrypted preferences
     * WARNING: This will delete all stored sensitive data
     */
    fun clearAll() {
        prefs.edit { clear() }
    }
}
