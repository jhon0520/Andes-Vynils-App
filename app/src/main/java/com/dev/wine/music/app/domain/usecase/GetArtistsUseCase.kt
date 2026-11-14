package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.repository.ArtistRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Musicians and bands sorted by name. The musicians endpoint does not say which band a
 * musician is in, so each musician gets it from the band that lists them as a member.
 * Matching and sorting run on the Default dispatcher, off the main thread.
 */
class GetArtistsUseCase @Inject constructor(
    private val repository: ArtistRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): List<Artist> {
        val artists = repository.getArtists()
        return withContext(defaultDispatcher) {
            val bandByMemberId = artists
                .filter { it.type == ArtistType.BAND }
                .flatMap { band -> band.memberIds.map { it to band.name } }
                .toMap()
            artists
                .map { if (it.type == ArtistType.MUSICIAN) it.copy(bandName = bandByMemberId[it.id]) else it }
                .sortedBy { it.name.lowercase() }
        }
    }
}
