package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.CollectorRepository
import javax.inject.Inject

class GetCollectorsUseCase @Inject constructor(
    private val repository: CollectorRepository,
) {
    suspend operator fun invoke(): List<Collector> =
        repository.getCollectors().sortedBy { it.name.lowercase() }
}
