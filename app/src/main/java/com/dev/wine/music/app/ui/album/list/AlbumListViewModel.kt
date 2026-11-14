package com.dev.wine.music.app.ui.album.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.usecase.GetAlbumsUseCase
import com.dev.wine.music.app.domain.usecase.ObserveSessionUseCase
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.common.toUiError
import com.dev.wine.music.app.util.fastMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Normalizer
import javax.inject.Inject

/** What the catalog shows: the albums that pass the search and the genre, plus how many were loaded. */
@Immutable
data class AlbumCatalog(
    val albums: List<Album>,
    val totalCount: Int,
)

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val getAlbums: GetAlbumsUseCase,
    observeSession: ObserveSessionUseCase,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    /** Who is browsing: the header shows it, and only a collector can create albums. */
    val session: StateFlow<UserSession?> = observeSession()

    private val loadedAlbums = MutableStateFlow<UiState<List<SearchableAlbum>>>(UiState.Loading)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedGenre = MutableStateFlow<AlbumGenre?>(null)
    val selectedGenre: StateFlow<AlbumGenre?> = _selectedGenre.asStateFlow()

    // Search and genre filter the list already in memory, so typing never calls the API.
    // flowOn runs the filtering on the Default dispatcher; the screen still collects on main.
    val uiState: StateFlow<UiState<AlbumCatalog>> =
        combine(loadedAlbums, _query, _selectedGenre) { state, query, genre ->
            when (state) {
                UiState.Loading -> UiState.Loading
                is UiState.Error -> state
                is UiState.Success -> {
                    // Normalize the query once per change, not once per album inside the loop.
                    val needle = query.normalized()
                    UiState.Success(
                        AlbumCatalog(
                            albums = state.data.mapNotNull { if (it.matches(needle, genre)) it.album else null },
                            totalCount = state.data.size,
                        ),
                    )
                }
            }
        }
            .flowOn(defaultDispatcher)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            loadedAlbums.value = UiState.Loading
            loadedAlbums.value = try {
                val albums = getAlbums()
                UiState.Success(withContext(defaultDispatcher) { albums.fastMap { it.searchable() } })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toUiError()
            }
        }
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    /** Null selects every genre ("Todos"). */
    fun onGenreSelect(genre: AlbumGenre?) {
        _selectedGenre.value = genre
    }

    fun clearFilters() {
        _query.value = ""
        _selectedGenre.value = null
    }

    /**
     * An album with its search text already normalized. Normalizing (Unicode decomposition plus a
     * regex) is the costly part of the search, so it runs once per album when the catalog loads
     * instead of once per album and performer on every keystroke.
     */
    private class SearchableAlbum(
        val album: Album,
        val genre: AlbumGenre?,
        val searchKeys: List<String>,
    ) {
        fun matches(needle: String, selectedGenre: AlbumGenre?): Boolean {
            if (selectedGenre != null && genre != selectedGenre) return false
            return needle.isEmpty() || searchKeys.any { it.contains(needle) }
        }
    }

    private fun Album.searchable(): SearchableAlbum {
        val keys = ArrayList<String>(performers.size + 1)
        keys.add(name.normalized())
        for (index in performers.indices) keys.add(performers[index].name.normalized())
        return SearchableAlbum(album = this, genre = genreType, searchKeys = keys)
    }

    /** Lowercase without accents, so "ruben" finds "Rubén". */
    private fun String.normalized(): String =
        Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD).replace(DIACRITICS, "")

    private companion object {
        val DIACRITICS = "\\p{Mn}+".toRegex()
    }
}
