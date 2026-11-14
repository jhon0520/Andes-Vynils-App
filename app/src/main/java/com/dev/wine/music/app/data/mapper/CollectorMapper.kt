package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.CollectorDto
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.util.fastMap

fun CollectorDto.toDomain(): Collector = Collector(
    id = id,
    name = name,
    telephone = telephone,
    email = email,
    commentIds = comments.fastMap { it.id },
)
