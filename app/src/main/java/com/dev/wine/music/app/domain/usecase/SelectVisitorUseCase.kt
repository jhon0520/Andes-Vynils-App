package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.repository.SessionRepository
import javax.inject.Inject

class SelectVisitorUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    operator fun invoke() = repository.selectVisitor()
}
