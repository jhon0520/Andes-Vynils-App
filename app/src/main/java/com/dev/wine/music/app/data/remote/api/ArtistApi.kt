package com.dev.wine.music.app.data.remote.api

import com.dev.wine.music.app.data.remote.dto.PerformerDto
import com.dev.wine.music.app.data.remote.dto.PerformerPrizeDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistApi {
    @GET("musicians")
    suspend fun getMusicians(): List<PerformerDto>

    @GET("bands")
    suspend fun getBands(): List<PerformerDto>

    @GET("musicians/{musicianId}")
    suspend fun getMusician(@Path("musicianId") musicianId: Int): PerformerDto

    @GET("bands/{bandId}")
    suspend fun getBand(@Path("bandId") bandId: Int): PerformerDto

    @GET("performerprizes")
    suspend fun getPerformerPrizes(): List<PerformerPrizeDto>
}
