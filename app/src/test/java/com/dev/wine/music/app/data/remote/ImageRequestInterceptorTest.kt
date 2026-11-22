package com.dev.wine.music.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ImageRequestInterceptorTest {

    private val seed = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/" +
        "800px-Ruben_Blades_by_Gage_Skidmore.jpg"

    @Test
    fun `a wikimedia thumbnail wider than allowed goes down to the largest allowed step`() {
        assertEquals(seed.replace("/800px-", "/500px-"), ImageRequestInterceptor.wikimediaThumbnail(seed))
    }

    @Test
    fun `a width between steps goes down to the previous step`() {
        assertEquals(seed.replace("/800px-", "/250px-"), ImageRequestInterceptor.wikimediaThumbnail(seed.replace("/800px-", "/320px-")))
    }

    @Test
    fun `other image urls are left as they are`() {
        assertNull(ImageRequestInterceptor.wikimediaThumbnail("https://i.pinimg.com/564x/aa/5f/ed/cover.jpg"))
    }

    @Test
    fun `every image request carries the user agent`() {
        MockWebServer().use { server ->
            server.enqueue(MockResponse())
            val client = OkHttpClient.Builder().addInterceptor(ImageRequestInterceptor("WineMusic/test")).build()

            client.newCall(Request.Builder().url(server.url("/cover.jpg")).build()).execute().close()

            assertEquals("WineMusic/test", server.takeRequest().getHeader("User-Agent"))
        }
    }
}
