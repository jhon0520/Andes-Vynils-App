package com.dev.wine.music.app.data.remote.api

import com.dev.wine.music.app.data.remote.dto.CollectorDto
import retrofit2.http.GET

interface CollectorApi {
    @GET("collectors")
    suspend fun getCollectors(): List<CollectorDto>
}
