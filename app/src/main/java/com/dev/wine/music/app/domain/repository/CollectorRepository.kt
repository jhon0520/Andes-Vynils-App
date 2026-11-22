package com.dev.wine.music.app.domain.repository

import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.CollectorAlbum
import com.dev.wine.music.app.domain.model.CollectorDetail

interface CollectorRepository {
    suspend fun getCollectors(): List<Collector>

    /** The collector with favorites and reviews; its collection comes from [getCollectorAlbums]. */
    suspend fun getCollector(collectorId: Int): CollectorDetail

    suspend fun getCollectorAlbums(collectorId: Int): List<CollectorAlbum>
}
