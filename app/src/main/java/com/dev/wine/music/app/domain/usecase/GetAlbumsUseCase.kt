package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.repository.AlbumRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: AlbumRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): List<Album> {
        val albums = repository.getAlbums()
        // Sorting is CPU work: it runs on the Default dispatcher, not on the main thread.
        return withContext(defaultDispatcher) { albums.sortedBy { it.name.lowercase() } }
    }
}
