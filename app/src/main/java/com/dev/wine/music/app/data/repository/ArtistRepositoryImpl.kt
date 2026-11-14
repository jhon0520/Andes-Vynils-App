package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.mapper.toDetail
import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.ArtistApi
import com.dev.wine.music.app.data.remote.safeApiCall
import com.dev.wine.music.app.domain.coroutines.IoDispatcher
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistDetail
import com.dev.wine.music.app.domain.model.ArtistPrize
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.repository.ArtistRepository
import com.dev.wine.music.app.util.fastForEach
import com.dev.wine.music.app.util.fastMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * "Artists" in the app are musicians and bands, fetched in parallel on [ioDispatcher] and merged.
 * The merged list is kept in [cache], so the artists tab does not call the API twice.
 */
class ArtistRepositoryImpl @Inject constructor(
    private val api: ArtistApi,
    private val cache: RepositoryCache,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ArtistRepository {

    override suspend fun getArtists(): List<Artist> {
        cache.artists?.let { return it }
        val artists: List<Artist> = withContext(ioDispatcher) {
            safeApiCall {
                coroutineScope {
                    val musicians = async { api.getMusicians() }
                    val bands = async { api.getBands() }
                    val musicianDtos = musicians.await()
                    val bandDtos = bands.await()
                    // One list sized for both, instead of two mapped lists plus the one `+` creates.
                    val merged = ArrayList<Artist>(musicianDtos.size + bandDtos.size)
                    musicianDtos.fastForEach { merged.add(it.toDomain(ArtistType.MUSICIAN)) }
                    bandDtos.fastForEach { merged.add(it.toDomain(ArtistType.BAND)) }
                    merged
                }
            }
        }
        cache.artists = artists
        return artists
    }

    /** Reuses the cached artist list when the artists tab already loaded it. */
    override suspend fun getBands(): List<Artist> {
        cache.artists?.let { artists -> return artists.filter { it.type == ArtistType.BAND } }
        return withContext(ioDispatcher) {
            safeApiCall { api.getBands().fastMap { it.toDomain(ArtistType.BAND) } }
        }
    }

    override suspend fun getArtist(artistId: Int, type: ArtistType): ArtistDetail = withContext(ioDispatcher) {
        safeApiCall {
            when (type) {
                ArtistType.MUSICIAN -> api.getMusician(artistId)
                ArtistType.BAND -> api.getBand(artistId)
            }.toDetail(type)
        }
    }

    override suspend fun getPerformerPrizes(): List<ArtistPrize> = withContext(ioDispatcher) {
        safeApiCall { api.getPerformerPrizes().fastMap { it.toDomain() } }
    }
}
