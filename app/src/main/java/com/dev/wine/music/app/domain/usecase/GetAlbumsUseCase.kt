package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.repository.AlbumRepository
import javax.inject.Inject

class GetAlbumsUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {
    suspend operator fun invoke(): List<Album> =
        repository.getAlbums().sortedBy { it.name.lowercase() }
}
