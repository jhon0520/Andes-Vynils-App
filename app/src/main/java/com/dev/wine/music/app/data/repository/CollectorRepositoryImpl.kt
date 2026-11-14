package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.CollectorApi
import com.dev.wine.music.app.data.remote.safeApiCall
import com.dev.wine.music.app.domain.coroutines.IoDispatcher
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.CollectorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** The network call, the JSON parsing and the mapping run on [ioDispatcher], never on the main thread. */
class CollectorRepositoryImpl @Inject constructor(
    private val api: CollectorApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> = withContext(ioDispatcher) {
        safeApiCall { api.getCollectors().map { it.toDomain() } }
    }
}
