package com.dev.wine.music.app.di

import com.dev.wine.music.app.BuildConfig
import com.dev.wine.music.app.data.remote.api.AlbumApi
import com.dev.wine.music.app.data.remote.api.ArtistApi
import com.dev.wine.music.app.data.remote.api.CollectorApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            },
        )
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideAlbumApi(retrofit: Retrofit): AlbumApi = retrofit.create(AlbumApi::class.java)

    @Provides
    @Singleton
    fun provideArtistApi(retrofit: Retrofit): ArtistApi = retrofit.create(ArtistApi::class.java)

    @Provides
    @Singleton
    fun provideCollectorApi(retrofit: Retrofit): CollectorApi = retrofit.create(CollectorApi::class.java)
}
