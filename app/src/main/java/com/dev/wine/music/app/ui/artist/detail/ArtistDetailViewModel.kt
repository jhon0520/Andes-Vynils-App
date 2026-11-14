package com.dev.wine.music.app.ui.artist.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.ArtistDetail
import com.dev.wine.music.app.domain.model.ArtistType
import com.dev.wine.music.app.domain.usecase.GetArtistDetailUseCase
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.common.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getArtistDetail: GetArtistDetailUseCase,
) : ViewModel() {

    // Navigation puts the ArtistDetailRoute arguments here under their property names.
    private val artistId: Int = checkNotNull(savedStateHandle[ARTIST_ID_KEY])
    private val type: ArtistType = ArtistType.valueOf(checkNotNull(savedStateHandle[TYPE_KEY]))

    private val _uiState = MutableStateFlow<UiState<ArtistDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<ArtistDetail>> = _uiState.asStateFlow()

    init {
        loadArtist()
    }

    fun loadArtist() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(getArtistDetail(artistId, type))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toUiError()
            }
        }
    }

    companion object {
        const val ARTIST_ID_KEY = "artistId"
        const val TYPE_KEY = "type"
    }
}
