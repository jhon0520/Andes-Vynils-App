package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.data.cache.RepositoryCache
import com.dev.wine.music.app.data.mapper.toDetail
import com.dev.wine.music.app.data.mapper.toDomain
import com.dev.wine.music.app.data.remote.api.CollectorApi
import com.dev.wine.music.app.data.remote.safeApiCall
import com.dev.wine.music.app.domain.coroutines.IoDispatcher
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.CollectorAlbum
import com.dev.wine.music.app.domain.model.CollectorDetail
import com.dev.wine.music.app.domain.repository.CollectorRepository
import com.dev.wine.music.app.util.fastMap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Answers from [cache] when it already has the collectors. The album detail asks for them every
 * time (to name the authors of the reviews), so the cache saves one request per album opened.
 * Otherwise the network call, the JSON parsing and the mapping run on [ioDispatcher].
 * The collector detail (HU06) is not cached: it always shows the collection as the backend has it now.
 */
class CollectorRepositoryImpl @Inject constructor(
    private val api: CollectorApi,
    private val cache: RepositoryCache,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CollectorRepository {

    override suspend fun getCollectors(): List<Collector> {
        cache.collectors?.let { return it }
        val collectors = withContext(ioDispatcher) {
            safeApiCall { api.getCollectors().fastMap { it.toDomain() } }
        }
        cache.collectors = collectors
        return collectors
    }

    override suspend fun getCollector(collectorId: Int): CollectorDetail = withContext(ioDispatcher) {
        safeApiCall { api.getCollector(collectorId).toDetail() }
    }

    override suspend fun getCollectorAlbums(collectorId: Int): List<CollectorAlbum> = withContext(ioDispatcher) {
        safeApiCall { api.getCollectorAlbums(collectorId).mapNotNull { it.toDomain() } }
    }
}
