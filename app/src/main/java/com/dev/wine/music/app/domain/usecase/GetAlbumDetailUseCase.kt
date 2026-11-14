package com.dev.wine.music.app.domain.usecase

import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.AlbumRepository
import com.dev.wine.music.app.domain.repository.CollectorRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * The album with the author of each review. The album endpoint sends comments without
 * their author, so the collectors are fetched in parallel and matched by comment id.
 * The matching runs on the Default dispatcher, off the main thread.
 */
class GetAlbumDetailUseCase @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val collectorRepository: CollectorRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(albumId: Int): Album = coroutineScope {
        val album = async { albumRepository.getAlbum(albumId) }
        val collectors = async { getCollectorsOrEmpty() }
        val loaded = album.await()
        val loadedCollectors = collectors.await()

        withContext(defaultDispatcher) {
            val authorByCommentId = loadedCollectors
                .flatMap { collector -> collector.commentIds.map { it to collector.name } }
                .toMap()
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
