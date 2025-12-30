package com.example.luminacal.util

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger

/**
 * Singleton ImageLoader with optimized caching for performance
 */
object ImageLoaderFactory {
    
    @Volatile
    private var imageLoader: ImageLoader? = null
    
    fun getInstance(context: Context): ImageLoader {
        return imageLoader ?: synchronized(this) {
            imageLoader ?: createImageLoader(context).also { imageLoader = it }
        }
    }
    
    private fun createImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            // Memory cache - 25% of available memory
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            // Disk cache - 50MB for offline/persistent caching
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }
            // Cache policies
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            // Crossfade for smooth loading
            .crossfade(true)
            .crossfade(200)
            // Respect cache headers
            .respectCacheHeaders(true)
            .build()
    }
    
    /**
     * Clear all caches (useful for debugging or user-triggered cache clear)
     */
    fun clearCache(context: Context) {
        getInstance(context).memoryCache?.clear()
        getInstance(context).diskCache?.clear()
    }
}
