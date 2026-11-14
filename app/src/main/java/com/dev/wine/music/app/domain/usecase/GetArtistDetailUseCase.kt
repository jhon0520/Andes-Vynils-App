package com.dev.wine.music.app.domain.usecase

import androidx.collection.SparseArrayCompat
import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.ArtistDetail
import com.dev.wine.music.app.domain.model.ArtistPrize
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.repository.ArtistRepository
import com.dev.wine.music.app.util.fastForEach
import com.dev.wine.music.app.util.fastMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * The musician or band with its prizes named and, for a musician, its band. The artist only
 * brings prize ids, so the prizes are matched by id with GET /performerprizes; the musician
 * endpoint does not say which band they are in, so it comes from the band that lists them.
 * Both extra calls run in parallel with the artist, and the matching runs on the Default dispatcher.
 */
class GetArtistDetailUseCase @Inject constructor(
    private val repository: ArtistRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(artistId: Int, type: ArtistType): ArtistDetail = coroutineScope {
        val detail = async { repository.getArtist(artistId, type) }
        val prizes = async { orEmpty { repository.getPerformerPrizes() } }
        val bands = async { if (type == ArtistType.MUSICIAN) orEmpty { repository.getBands() } else emptyList() }
        val loaded = detail.await()
        val catalog = prizes.await()
        val loadedBands = bands.await()

        withContext(defaultDispatcher) {
            // Prize by id. SparseArrayCompat keys are plain ints: no Integer boxing, unlike a HashMap.
            val prizeById = SparseArrayCompat<ArtistPrize>(catalog.size)
            catalog.fastForEach { prizeById.put(it.id, it) }
            val band = loadedBands.firstOrNull { artistId in it.memberIds }
            loaded.copy(
                artist = if (band != null) loaded.artist.copy(bandName = band.name) else loaded.artist,
                band = band,
                albums = loaded.albums.sortedBy { it.releaseDate },
                prizes = loaded.prizes
                    .fastMap { prize -> prizeById.get(prize.id)?.copy(premiationDate = prize.premiationDate) ?: prize }
                    .sortedBy { it.premiationDate },
            )
        }
    }

    /** Prize names and the band are a nice-to-have: if they fail, the artist still shows. */
    private suspend fun <T> orEmpty(block: suspend () -> List<T>): List<T> = try {
        block()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        emptyList()
    }
}
