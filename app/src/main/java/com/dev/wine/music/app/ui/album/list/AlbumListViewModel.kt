package com.dev.wine.music.app.ui.album.list

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.AlbumGenre
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.usecase.GetAlbumsUseCase
import com.dev.wine.music.app.domain.usecase.ObserveSessionUseCase
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.common.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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
) : ViewModel() {

    /** Who is browsing: the header shows it, and only a collector can create albums. */
    val session: StateFlow<UserSession?> = observeSession()

    private val loadedAlbums = MutableStateFlow<UiState<List<Album>>>(UiState.Loading)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedGenre = MutableStateFlow<AlbumGenre?>(null)
    val selectedGenre: StateFlow<AlbumGenre?> = _selectedGenre.asStateFlow()

    // Search and genre filter the list already in memory, so typing never calls the API.
    val uiState: StateFlow<UiState<AlbumCatalog>> =
        combine(loadedAlbums, _query, _selectedGenre) { state, query, genre ->
            when (state) {
                UiState.Loading -> UiState.Loading
                is UiState.Error -> state
                is UiState.Success -> UiState.Success(
                    AlbumCatalog(
                        albums = state.data.filter { it.matches(query.normalized(), genre) },
                        totalCount = state.data.size,
                    ),
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            loadedAlbums.value = UiState.Loading
            loadedAlbums.value = try {
                UiState.Success(getAlbums())
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

    private fun Album.matches(query: String, genre: AlbumGenre?): Boolean {
        if (genre != null && genreType != genre) return false
        if (query.isEmpty()) return true
        return name.normalized().contains(query) || performers.any { it.name.normalized().contains(query) }
    }

    /** Lowercase without accents, so "ruben" finds "Rubén". */
    private fun String.normalized(): String =
        Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD).replace(DIACRITICS, "")

    private companion object {
        val DIACRITICS = "\\p{Mn}+".toRegex()
    }
}
