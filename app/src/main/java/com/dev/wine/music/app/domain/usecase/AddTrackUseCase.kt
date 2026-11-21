package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.NewTrack
import com.dev.wine.music.app.domain.model.Track
import com.dev.wine.music.app.domain.repository.AlbumRepository
import javax.inject.Inject

class AddTrackUseCase @Inject constructor(
    private val repository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: Int, newTrack: NewTrack): Track = repository.addTrack(albumId, newTrack)
}
