package com.example.luminacal

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.luminacal.util.ImageLoaderFactory as LuminaImageLoaderFactory

/**
 * Application class for global initialization
 * - Coil ImageLoader with custom caching
 * - Future: WorkManager, Analytics, etc.
 */
class LuminaCalApplication : Application(), ImageLoaderFactory {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Coil with custom ImageLoader
        Coil.setImageLoader(LuminaImageLoaderFactory.getInstance(this))
    }
    
    override fun newImageLoader(): ImageLoader {
        return LuminaImageLoaderFactory.getInstance(this)
    }
}
