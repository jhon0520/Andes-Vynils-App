package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.repository.ArtistRepository
import javax.inject.Inject

/**
 * Musicians and bands sorted by name. The musicians endpoint does not say which band a
 * musician is in, so each musician gets it from the band that lists them as a member.
 */
class GetArtistsUseCase @Inject constructor(
    private val repository: ArtistRepository,
) {
    suspend operator fun invoke(): List<Artist> {
        val artists = repository.getArtists()
        val bandByMemberId = artists
            .filter { it.type == ArtistType.BAND }
            .flatMap { band -> band.memberIds.map { it to band.name } }
            .toMap()
        return artists
            .map { if (it.type == ArtistType.MUSICIAN) it.copy(bandName = bandByMemberId[it.id]) else it }
            .sortedBy { it.name.lowercase() }
    }
}
