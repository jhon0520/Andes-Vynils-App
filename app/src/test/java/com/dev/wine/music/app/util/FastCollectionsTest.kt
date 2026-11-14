package com.dev.wine.music.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FastCollectionsTest {

    @Test
    fun `fastMap keeps the order and the size`() {
        assertEquals(listOf(2, 4, 6), listOf(1, 2, 3).fastMap { it * 2 })
    }

    @Test
    fun `fastMap of an empty list is empty`() {
        assertTrue(emptyList<Int>().fastMap { it * 2 }.isEmpty())
    }

    @Test
    fun `fastForEach visits every element in order`() {
        val visited = mutableListOf<String>()

        listOf("A1", "A2", "B1").fastForEach { visited.add(it) }

        assertEquals(listOf("A1", "A2", "B1"), visited)
    }
}
