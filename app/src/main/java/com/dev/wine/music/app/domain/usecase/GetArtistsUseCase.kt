package com.dev.wine.music.app.domain.usecase

import androidx.collection.SparseArrayCompat
import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.repository.ArtistRepository
import com.dev.wine.music.app.util.fastForEach
import com.dev.wine.music.app.util.fastMap
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
            // Band name by member id. SparseArrayCompat keys are plain ints: no Integer boxing and
            // no Pair per member, unlike flatMap { it to name }.toMap(), which built a HashMap.
            val bandByMemberId = SparseArrayCompat<String>()
            artists.fastForEach { artist ->
                if (artist.type == ArtistType.BAND) {
                    artist.memberIds.fastForEach { memberId -> bandByMemberId.put(memberId, artist.name) }
                }
            }
            artists
                .fastMap { if (it.type == ArtistType.MUSICIAN) it.copy(bandName = bandByMemberId.get(it.id)) else it }
                .sortedWith(BY_NAME)
        }
    }

    private companion object {
        /** Compares ignoring case without creating a lowercase copy of each name on every comparison. */
        val BY_NAME = compareBy<Artist, String>(String.CASE_INSENSITIVE_ORDER) { it.name }
    }
}
