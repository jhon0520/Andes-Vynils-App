package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.NewAlbum
import com.dev.wine.music.app.domain.model.RecordLabel
import com.dev.wine.music.app.fakes.FakeAlbumRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateAlbumUseCaseTest {

    @Test
    fun `sends the album to the repository and returns the created one`() = runTest {
        val repository = FakeAlbumRepository()
        val newAlbum = NewAlbum("Siembra", "https://example.com/a.jpg", "1978-08-01T00:00:00-05:00", "Salsa", AlbumGenre.SALSA, RecordLabel.FANIA_RECORDS)

        val created = CreateAlbumUseCase(repository)(newAlbum)

        assertEquals(FakeAlbumRepository.CREATED_ALBUM_ID, created.id)
        assertEquals(listOf(newAlbum), repository.createdAlbums)
    }
}
