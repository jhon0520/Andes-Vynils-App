package com.dev.wine.music.app.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.Artist
import com.dev.wine.music.app.domain.usecase.GetArtistsUseCase
import com.dev.wine.music.app.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistListViewModel @Inject constructor(
    private val getArtists: GetArtistsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Artist>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Artist>>> = _uiState.asStateFlow()

    init {
        loadArtists()
    }

    fun loadArtists() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(getArtists())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.message)
            }
        }
    }
}
