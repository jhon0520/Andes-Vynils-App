package com.dev.wine.music.app

import android.app.Application
import android.content.ComponentCallbacks2
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.dev.wine.music.app.data.cache.RepositoryCache
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Entry point for Hilt: generates the app-wide dependency container.
 *
 * It also sets how the app uses memory:
 * - Coil keeps decoded covers in a memory cache capped at [IMAGE_MEMORY_PERCENT] of the app's
 *   memory and the downloaded files in a disk cache of [IMAGE_DISK_CACHE_BYTES], so scrolling
 *   the catalog does not download or decode the same cover twice.
 * - Covers are opaque JPEGs, so they are decoded as RGB_565 (2 bytes per pixel instead of 4).
 * - When the system asks the app to trim memory, the repository cache is dropped
 *   (Coil already trims its own memory cache).
 */
@HiltAndroidApp
class WineMusicApp : Application(), ImageLoaderFactory {

    @Inject
    lateinit var repositoryCache: RepositoryCache

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(this)
        .memoryCache {
            MemoryCache.Builder(this)
                .maxSizePercent(IMAGE_MEMORY_PERCENT)
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(cacheDir.resolve(IMAGE_CACHE_DIR))
                .maxSizeBytes(IMAGE_DISK_CACHE_BYTES)
                .build()
        }
        // Cover URLs point to external sites whose headers often forbid caching.
        .respectCacheHeaders(false)
        .allowRgb565(true)
        .crossfade(true)
        .build()

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // The app is in the background and the system is reclaiming memory: the data can be
        // fetched again when the user comes back.
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND && ::repositoryCache.isInitialized) {
            repositoryCache.clear()
        }
    }

    private companion object {
        const val IMAGE_MEMORY_PERCENT = 0.20
        const val IMAGE_DISK_CACHE_BYTES = 50L * 1024 * 1024
        const val IMAGE_CACHE_DIR = "image_cache"
    }
}
