package com.dev.wine.music.app.ui.artist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.usecase.GetArtistsUseCase
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
import javax.inject.Inject

/** What the list shows: the artists of the selected type, plus how many were loaded. */
@Immutable
data class ArtistCatalog(
    val artists: List<Artist>,
    val totalCount: Int,
)

@HiltViewModel
class ArtistListViewModel @Inject constructor(
    private val getArtists: GetArtistsUseCase,
) : ViewModel() {

    private val loadedArtists = MutableStateFlow<UiState<List<Artist>>>(UiState.Loading)

    private val _selectedType = MutableStateFlow<ArtistType?>(null)
    val selectedType: StateFlow<ArtistType?> = _selectedType.asStateFlow()

    // The type filter works on the list already in memory, so switching tabs never calls the API.
    val uiState: StateFlow<UiState<ArtistCatalog>> =
        combine(loadedArtists, _selectedType) { state, type ->
            when (state) {
                UiState.Loading -> UiState.Loading
                is UiState.Error -> state
                is UiState.Success -> UiState.Success(
                    ArtistCatalog(
                        artists = state.data.filter { type == null || it.type == type },
                        totalCount = state.data.size,
                    ),
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    init {
        loadArtists()
    }

    fun loadArtists() {
        viewModelScope.launch {
            loadedArtists.value = UiState.Loading
            loadedArtists.value = try {
                UiState.Success(getArtists())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toUiError()
            }
        }
    }

    /** Null selects both musicians and bands ("Todos"). */
    fun onTypeSelect(type: ArtistType?) {
        _selectedType.value = type
    }
}
