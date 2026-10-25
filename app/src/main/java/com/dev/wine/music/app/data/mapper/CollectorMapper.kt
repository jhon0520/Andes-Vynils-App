package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.CollectorDto
import com.dev.wine.music.app.domain.model.Collector

fun CollectorDto.toDomain(): Collector = Collector(
    id = id,
    name = name,
    telephone = telephone,
    email = email,
)
