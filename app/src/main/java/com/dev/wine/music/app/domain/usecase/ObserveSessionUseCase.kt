package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    operator fun invoke(): StateFlow<UserSession?> = repository.session
}
