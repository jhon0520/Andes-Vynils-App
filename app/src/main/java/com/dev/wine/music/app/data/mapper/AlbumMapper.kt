package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.data.remote.dto.CommentDto
import com.dev.wine.music.app.data.remote.dto.TrackDto
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Comment
import com.dev.wine.music.app.domain.model.Track

fun AlbumDto.toDomain(): Album = Album(
    id = id,
    name = name,
    cover = cover,
    releaseDate = releaseDate,
    description = description,
    genre = genre,
    recordLabel = recordLabel,
    tracks = tracks.map { it.toDomain() },
    performers = performers.map { it.toDomain() },
    comments = comments.map { it.toDomain() },
)

fun TrackDto.toDomain(): Track = Track(
    id = id,
    name = name,
    duration = duration,
)

/** The album endpoint sends comments without their author; the use case fills it in. */
fun CommentDto.toDomain(): Comment = Comment(
    id = id,
    description = description,
    rating = rating.coerceIn(0, Comment.MAX_RATING),
)
