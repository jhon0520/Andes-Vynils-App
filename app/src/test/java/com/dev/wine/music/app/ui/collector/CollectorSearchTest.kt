package com.dev.wine.music.app.ui.collector

import com.dev.wine.music.app.fakes.TestData
import org.junit.Assert.assertEquals
import org.junit.Test

class CollectorSearchTest {

    private val manolo = TestData.collector(id = 100, name = "Manolo Bellón", email = "manollo@caracol.com.co")
    private val jaime = TestData.collector(id = 101, name = "Jaime Monsalve", email = "jmonsalve@rtvc.com.co")

    @Test
    fun `initials take the first letter of the first two words`() {
        assertEquals("MB", collectorInitials("Manolo Bellón"))
        assertEquals("JM", collectorInitials("  Jaime   Monsalve Ruiz "))
        assertEquals("J", collectorInitials("Jaime"))
        assertEquals("··", collectorInitials("  "))
    }

    @Test
    fun `search ignores case and accents and also looks at the email`() {
        val collectors = listOf(manolo, jaime)

        assertEquals(listOf(manolo), collectors.filterBy("bellon"))
        assertEquals(listOf(manolo), collectors.filterBy("CARACOL"))
        assertEquals(listOf(jaime), collectors.filterBy(" monsalve "))
        assertEquals(collectors, collectors.filterBy(""))
        assertEquals(emptyList<Any>(), collectors.filterBy("zzz"))
    }
}
