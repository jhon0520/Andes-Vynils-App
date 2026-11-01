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

    @Test
    fun `releaseDay shows the date as day, month and year`() {
        assertEquals("01/08/1984", TestData.album(releaseDate = "1984-08-01T05:00:00.000Z").releaseDay)
    }

    @Test
    fun `numberedTracks puts the first half on side A and the rest on side B`() {
        val album = TestData.album(tracks = (1..5).map { TestData.track(id = it) })

        assertEquals(listOf("A1", "A2", "A3", "B1", "B2"), album.numberedTracks.map { it.position })
    }

    @Test
    fun `numberedTracks is empty for an album without tracks`() {
        assertEquals(emptyList<NumberedTrack>(), TestData.album().numberedTracks)
    }
}
