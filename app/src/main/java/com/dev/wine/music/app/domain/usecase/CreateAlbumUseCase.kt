package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.NewAlbum
import com.dev.wine.music.app.domain.repository.AlbumRepository
import javax.inject.Inject

class CreateAlbumUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {
    suspend operator fun invoke(newAlbum: NewAlbum): Album = repository.createAlbum(newAlbum)
}
