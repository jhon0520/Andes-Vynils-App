package com.dev.wine.music.app.domain.repository

import com.dev.wine.music.app.domain.model.Artist

interface ArtistRepository {
    suspend fun getArtists(): List<Artist>
}
