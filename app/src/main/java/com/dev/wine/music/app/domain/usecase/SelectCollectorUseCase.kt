package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.SessionRepository
import javax.inject.Inject

class SelectCollectorUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    operator fun invoke(collector: Collector) = repository.selectCollector(collector)
}
