package com.dev.wine.music.app.domain.usecase

import androidx.collection.SparseArrayCompat
import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.CollectorDetail
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
 * The collector (HU06) with its collection and the album of each review. GET /collectors/{id}
 * does not bring the collection with its albums, so it comes from GET /collectors/{id}/albums;
 * its reviews do not say which album they belong to, so they are matched by id with the
 * comments of the catalog (GET /albums, usually already cached). The three calls run in
 * parallel and the matching runs on the Default dispatcher.
 */
class GetCollectorDetailUseCase @Inject constructor(
    private val collectorRepository: CollectorRepository,
    private val albumRepository: AlbumRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke(collectorId: Int): CollectorDetail = coroutineScope {
        val detail = async { collectorRepository.getCollector(collectorId) }
        val collection = async { collectorRepository.getCollectorAlbums(collectorId) }
        val catalog = async { catalogOrEmpty() }
        val loaded = detail.await()
        val albums = collection.await()
        val catalogAlbums = catalog.await()

        withContext(defaultDispatcher) {
            // Album by comment id. SparseArrayCompat keys are plain ints: no Integer boxing.
            val albumByComment = SparseArrayCompat<Album>()
            catalogAlbums.fastForEach { album -> album.comments.fastForEach { albumByComment.put(it.id, album) } }
            loaded.copy(
                favoriteArtists = loaded.favoriteArtists.sortedBy { it.name.lowercase() },
                albums = albums.sortedBy { it.album.name.lowercase() },
                reviews = loaded.reviews.fastMap { it.copy(album = albumByComment.get(it.id)) },
            )
        }
    }

    /** The album of each review is a nice-to-have: if the catalog fails, the collector still shows. */
    private suspend fun catalogOrEmpty(): List<Album> = try {
        albumRepository.getAlbums()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        emptyList()
    }
}
