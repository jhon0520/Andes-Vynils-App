package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.CollectorApi
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.CollectorRepository
import javax.inject.Inject

class CollectorRepositoryImpl @Inject constructor(
    private val api: CollectorApi,
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> =
        api.getCollectors().map { it.toDomain() }
}
