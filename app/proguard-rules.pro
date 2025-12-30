# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line number information for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ============== Room Database ==============
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.* <fields>;
    @androidx.room.* <methods>;
}
-keep class com.example.luminacal.data.local.** { *; }

# ============== Kotlin Serialization ==============
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# ============== Coil Image Loading ==============
-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ============== ML Kit ==============
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_common.** { *; }
-dontwarn com.google.mlkit.**

# ============== CameraX ==============
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ============== Compose ==============
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ============== Model Classes ==============
-keep class com.example.luminacal.model.** { *; }

# ============== Data Classes ==============
-keep class com.example.luminacal.data.** { *; }

# ============== Prevent obfuscation of view models ==============
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# ============== Keep Parcelable implementations ==============
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ============== Prevent crashes with reflection ==============
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}