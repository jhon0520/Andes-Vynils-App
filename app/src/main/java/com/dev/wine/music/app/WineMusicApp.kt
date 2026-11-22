package com.dev.wine.music.app

import android.app.Application
import android.content.ComponentCallbacks2
import android.os.StrictMode
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.remote.ImageRequestInterceptor
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
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
 * - Image requests identify the app with a User-Agent: Wikimedia refuses them otherwise
 *   (see [ImageRequestInterceptor]).
 * - Debug builds turn on StrictMode, which logs disk or network work on the main thread.
 */
@HiltAndroidApp
class WineMusicApp : Application(), ImageLoaderFactory {

    @Inject
    lateinit var repositoryCache: RepositoryCache

    override fun onCreate() {
        if (BuildConfig.DEBUG) enableStrictMode()
        super.onCreate()
    }

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
        .okHttpClient {
            OkHttpClient.Builder()
                .addInterceptor(ImageRequestInterceptor(IMAGE_USER_AGENT))
                .build()
        }
        // Cover URLs point to external sites whose headers often forbid caching.
        .respectCacheHeaders(false)
        .allowRgb565(true)
        .crossfade(true)
        .build()

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // The app is in the background and the system is reclaiming memory, or the app is in the
        // foreground but the device is about to kill processes: the data can be fetched again.
        @Suppress("DEPRECATION") // RUNNING_CRITICAL still arrives on Android 13 and older.
        val reclaim = level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND ||
            level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
        if (reclaim && ::repositoryCache.isInitialized) {
            repositoryCache.clear()
        }
    }

    /** Logs (tag StrictMode) disk or network access on the main thread and leaked resources. */
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build(),
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .detectLeakedSqlLiteObjects()
                .detectActivityLeaks()
                .penaltyLog()
                .build(),
        )
    }

    private companion object {
        const val IMAGE_MEMORY_PERCENT = 0.20
        const val IMAGE_DISK_CACHE_BYTES = 50L * 1024 * 1024
        const val IMAGE_CACHE_DIR = "image_cache"
        const val IMAGE_USER_AGENT =
            "WineMusic/${BuildConfig.VERSION_NAME} (Android; https://github.com/jhon0520/Andes-Vynils-App)"
    }
}
