package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.CollectorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCollectorsUseCase @Inject constructor(
    private val repository: CollectorRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(): List<Collector> {
        val collectors = repository.getCollectors()
        // Sorting is CPU work: it runs on the Default dispatcher, not on the main thread.
        return withContext(defaultDispatcher) { collectors.sortedWith(BY_NAME) }
    }

    private companion object {
        /** Compares ignoring case without creating a lowercase copy of each name on every comparison. */
        val BY_NAME = compareBy<Collector, String>(String.CASE_INSENSITIVE_ORDER) { it.name }
    }
}
