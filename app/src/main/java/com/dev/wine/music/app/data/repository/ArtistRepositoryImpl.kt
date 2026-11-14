package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.ArtistApi
import com.dev.wine.music.app.data.remote.safeApiCall
import com.dev.wine.music.app.domain.coroutines.IoDispatcher
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.repository.ArtistRepository
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
        val artists = withContext(ioDispatcher) {
            safeApiCall {
                coroutineScope {
                    val musicians = async { api.getMusicians() }
                    val bands = async { api.getBands() }
                    musicians.await().map { it.toDomain(ArtistType.MUSICIAN) } +
                        bands.await().map { it.toDomain(ArtistType.BAND) }
                }
            }
        }
        cache.artists = artists
        return artists
    }
}
