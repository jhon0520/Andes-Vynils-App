package com.dev.wine.music.app.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp interceptor for the image loader (Coil). Covers and artist photos come from external
 * sites, and Wikimedia (the seed photo of Rubén Blades) rejects requests that:
 * - do not identify the app with a User-Agent (403), or
 * - ask for a thumbnail width outside its fixed steps, such as the 800px of the seed URL (400).
 *
 * So every image request carries [userAgent], and Wikimedia thumbnails are moved to the closest
 * allowed width, never above [MAX_THUMB_WIDTH] (enough for a 160 dp image on a phone).
 */
class ImageRequestInterceptor(private val userAgent: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        return chain.proceed(
            request.newBuilder()
                .url(wikimediaThumbnail(url) ?: url)
                .header("User-Agent", userAgent)
                .build(),
        )
    }

    companion object {
        /** Widths Wikimedia renders for thumbnails (https://w.wiki/GHai). */
        private val THUMB_STEPS = intArrayOf(120, 250, 330, 500, 960)
        const val MAX_THUMB_WIDTH = 500

        private val WIKIMEDIA_THUMB = Regex("""^(https://upload\.wikimedia\.org/.+/thumb/.+/)(\d+)px-([^/]+)$""")

        /** The same thumbnail at an allowed width, or null when [url] is not a Wikimedia thumbnail. */
        fun wikimediaThumbnail(url: String): String? {
            val match = WIKIMEDIA_THUMB.matchEntire(url) ?: return null
            val (prefix, width, file) = match.destructured
            val wanted = minOf(width.toInt(), MAX_THUMB_WIDTH)
            val allowed = THUMB_STEPS.lastOrNull { it <= wanted } ?: THUMB_STEPS.first()
            return "$prefix${allowed}px-$file"
        }
    }
}
