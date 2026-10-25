package com.dev.wine.music.app.data.remote.api

import com.dev.wine.music.app.data.remote.dto.PerformerDto
import retrofit2.http.GET

interface ArtistApi {
    @GET("musicians")
    suspend fun getMusicians(): List<PerformerDto>

    @GET("bands")
    suspend fun getBands(): List<PerformerDto>
}
