package com.dev.wine.music.app.domain.model

/** What a collector fills in to add a track. [duration] is "mm:ss". */
data class NewTrack(
    val name: String,
    val duration: String,
)
