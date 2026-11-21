package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.NewTrack
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AddTrackUseCaseTest {

    @Test
    fun `adds the track to the given album`() = runTest {
        val repository = FakeAlbumRepository()
        val newTrack = NewTrack("Decisiones", "05:05")

        val track = AddTrackUseCase(repository)(100, newTrack)

        assertEquals("Decisiones", track.name)
        assertEquals(listOf(100 to newTrack), repository.addedTracks)
    }
}
