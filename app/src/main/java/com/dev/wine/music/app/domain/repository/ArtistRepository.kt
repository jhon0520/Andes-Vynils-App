package com.dev.wine.music.app.domain.repository

import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistDetail
import com.dev.wine.music.app.domain.model.ArtistPrize
import com.dev.wine.music.app.domain.model.ArtistType

interface ArtistRepository {
    suspend fun getArtists(): List<Artist>

    /** Bands with the ids of their members. */
    suspend fun getBands(): List<Artist>

    /** The musician or band with [artistId]; its prizes come without name and organization. */
    suspend fun getArtist(artistId: Int, type: ArtistType): ArtistDetail

    /** Every prize given to a performer, with its name and organization. */
    suspend fun getPerformerPrizes(): List<ArtistPrize>
}
