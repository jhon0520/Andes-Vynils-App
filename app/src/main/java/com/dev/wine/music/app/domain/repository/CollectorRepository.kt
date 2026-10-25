package com.dev.wine.music.app.domain.repository

import com.dev.wine.music.app.domain.model.Collector

interface CollectorRepository {
    suspend fun getCollectors(): List<Collector>
}
