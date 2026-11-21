package com.dev.wine.music.app.data.mapper

import com.dev.wine.music.app.data.remote.dto.AlbumDto
import com.dev.wine.music.app.data.remote.dto.AlbumRequestDto
import com.dev.wine.music.app.data.remote.dto.CommentDto
import com.dev.wine.music.app.data.remote.dto.TrackDto
import com.dev.wine.music.app.data.remote.dto.TrackRequestDto
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Comment
import com.dev.wine.music.app.domain.model.NewAlbum
import com.dev.wine.music.app.domain.model.NewTrack
import com.dev.wine.music.app.domain.model.Track
import com.dev.wine.music.app.util.fastMap

fun AlbumDto.toDomain(): Album = Album(
    id = id,
    name = name,
    cover = cover,
    releaseDate = releaseDate,
    description = description,
    genre = genre,
    recordLabel = recordLabel,
    tracks = tracks.fastMap { it.toDomain() },
    performers = performers.fastMap { it.toDomain() },
    comments = comments.fastMap { it.toDomain() },
)

fun NewAlbum.toRequest(): AlbumRequestDto = AlbumRequestDto(
    name = name.trim(),
    cover = cover.trim(),
    releaseDate = releaseDate,
    description = description.trim(),
    genre = genre.apiValue,
    recordLabel = recordLabel.apiValue,
)

fun TrackDto.toDomain(): Track = Track(
    id = id,
    name = name,
    duration = duration,
)

fun NewTrack.toTrackRequest(): TrackRequestDto = TrackRequestDto(
    name = name.trim(),
    duration = duration,
)

/** The album endpoint sends comments without their author; the use case fills it in. */
fun CommentDto.toDomain(): Comment = Comment(
    id = id,
    description = description,
    rating = rating.coerceIn(0, Comment.MAX_RATING),
)
