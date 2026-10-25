package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.ArtistApi
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.repository.ArtistRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/** "Artists" in the app are musicians and bands, fetched in parallel and merged. */
class ArtistRepositoryImpl @Inject constructor(
    private val api: ArtistApi,
) : ArtistRepository {

    override suspend fun getArtists(): List<Artist> = coroutineScope {
        val musicians = async { api.getMusicians() }
        val bands = async { api.getBands() }
        musicians.await().map { it.toDomain(ArtistType.MUSICIAN) } +
            bands.await().map { it.toDomain(ArtistType.BAND) }
    }
}
