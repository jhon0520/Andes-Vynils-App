package com.dev.wine.music.app.data.remote.api

import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.data.remote.dto.AlbumRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AlbumApi {
    @GET("albums")
    suspend fun getAlbums(): List<AlbumDto>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") albumId: Int): AlbumDto

    @POST("albums")
    suspend fun createAlbum(@Body body: AlbumRequestDto): AlbumDto
}
