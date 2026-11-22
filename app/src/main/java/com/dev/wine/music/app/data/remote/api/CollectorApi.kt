package com.dev.wine.music.app.data.remote.api

import com.dev.wine.music.app.data.remote.dto.CollectorAlbumDto
import com.dev.wine.music.app.data.remote.dto.CollectorDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CollectorApi {
    @GET("collectors")
    suspend fun getCollectors(): List<CollectorDto>

    @GET("collectors/{id}")
    suspend fun getCollector(@Path("id") collectorId: Int): CollectorDto

    @GET("collectors/{id}/albums")
    suspend fun getCollectorAlbums(@Path("id") collectorId: Int): List<CollectorAlbumDto>
}
