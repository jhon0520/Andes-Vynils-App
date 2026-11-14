package com.dev.wine.music.app.domain.usecase

import androidx.collection.SparseArrayCompat
import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.repository.AlbumRepository
import com.dev.wine.music.app.domain.repository.CollectorRepository
import com.dev.wine.music.app.util.fastForEach
import com.dev.wine.music.app.util.fastMap
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
            // Author by comment id. SparseArrayCompat keys are plain ints: no Integer boxing and
            // no Pair per comment, unlike flatMap { it to name }.toMap(), which built a HashMap.
            val authorByCommentId = SparseArrayCompat<String>()
            loadedCollectors.fastForEach { collector ->
                collector.commentIds.fastForEach { commentId -> authorByCommentId.put(commentId, collector.name) }
            }
            loaded.copy(comments = loaded.comments.fastMap { it.copy(author = authorByCommentId.get(it.id)) })
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
