package com.dev.wine.music.app.data.repository

import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keeps the session in memory only. On purpose there is no DataStore: when the process dies the
 * session is lost and the user picks a profile again.
 */
@Singleton
class InMemorySessionRepository @Inject constructor() : SessionRepository {

    private val _session = MutableStateFlow<UserSession?>(null)
    override val session: StateFlow<UserSession?> = _session.asStateFlow()

    override fun selectVisitor() {
        _session.value = UserSession.Visitor
    }

    override fun selectCollector(collector: Collector) {
        _session.value = UserSession.Collector(collector.id, collector.name, collector.email)
    }

    override fun clear() {
        _session.value = null
    }
}
