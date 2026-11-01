package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.AlbumRepository
import com.dev.wine.music.app.domain.repository.CollectorRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * The album with the author of each review. The album endpoint sends comments without
 * their author, so the collectors are fetched in parallel and matched by comment id.
 */
class GetAlbumDetailUseCase @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val collectorRepository: CollectorRepository,
) {
    suspend operator fun invoke(albumId: Int): Album = coroutineScope {
        val album = async { albumRepository.getAlbum(albumId) }
        val collectors = async { getCollectorsOrEmpty() }

        val authorByCommentId = collectors.await()
            .flatMap { collector -> collector.commentIds.map { it to collector.name } }
            .toMap()
        album.await().let { loaded ->
            loaded.copy(comments = loaded.comments.map { it.copy(author = authorByCommentId[it.id]) })
        }
    }

    /** Authors are a nice-to-have: if collectors fail, the album still shows with anonymous reviews. */
    private suspend fun getCollectorsOrEmpty(): List<Collector> = try {
        collectorRepository.getCollectors()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        emptyList()
    }
}
