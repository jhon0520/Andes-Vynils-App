package com.dev.wine.music.app.domain.repository

import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.model.UserSession
import kotlinx.coroutines.flow.StateFlow

/** The profile chosen in the onboarding. Null means nobody has chosen yet. */
interface SessionRepository {
    val session: StateFlow<UserSession?>
    fun selectVisitor()
    fun selectCollector(collector: Collector)
    fun clear()
}
