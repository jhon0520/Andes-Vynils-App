package com.dev.wine.music.app.data.remote.api

import com.dev.wine.music.app.data.remote.dto.AlbumDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumApi {
    @GET("albums")
    suspend fun getAlbums(): List<AlbumDto>

    @GET("albums/{id}")
    suspend fun getAlbum(@Path("id") albumId: Int): AlbumDto
}
