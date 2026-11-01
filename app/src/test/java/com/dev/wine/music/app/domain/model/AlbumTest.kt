package com.dev.wine.music.app.domain.model

import com.dev.wine.music.app.fakes.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlbumTest {

    @Test
    fun `genreType translates the backend value`() {
        assertEquals(AlbumGenre.CLASSICAL, TestData.album(genre = "Classical").genreType)
    }

    @Test
    fun `genreType is null for a genre the app does not know`() {
        assertNull(TestData.album(genre = "Jazz").genreType)
    }

    @Test
    fun `performerNames joins every performer`() {
        val album = TestData.album(
            performers = listOf(TestData.artist(id = 1, name = "Rubén Blades"), TestData.artist(id = 2, name = "Queen")),
        )

        assertEquals("Rubén Blades, Queen", album.performerNames)
    }
}
