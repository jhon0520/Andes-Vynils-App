package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.repository.ArtistRepository
import javax.inject.Inject

class GetArtistsUseCase @Inject constructor(
    private val repository: ArtistRepository,
) {
    suspend operator fun invoke(): List<Artist> =
        repository.getArtists().sortedBy { it.name.lowercase() }
}
