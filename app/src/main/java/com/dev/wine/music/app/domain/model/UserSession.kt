package com.dev.wine.music.app.domain.model

/** Who is using the app right now. It only lives while the app process is alive. */
sealed interface UserSession {
    data object Visitor : UserSession
    data class Collector(val id: Int, val name: String, val email: String) : UserSession
}
