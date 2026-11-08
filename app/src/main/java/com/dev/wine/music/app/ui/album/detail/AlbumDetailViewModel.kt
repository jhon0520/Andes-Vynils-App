package com.dev.wine.music.app.ui.album.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.model.UserSession
import com.dev.wine.music.app.domain.usecase.GetAlbumDetailUseCase
import com.dev.wine.music.app.domain.usecase.ObserveSessionUseCase
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
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAlbumDetail: GetAlbumDetailUseCase,
    observeSession: ObserveSessionUseCase,
) : ViewModel() {

    /** Who is browsing: only a collector can add tracks. */
    val session: StateFlow<UserSession?> = observeSession()

    // Navigation puts the AlbumDetailRoute.albumId argument here under its property name.
    private val albumId: Int = checkNotNull(savedStateHandle[ALBUM_ID_KEY])

    private val _uiState = MutableStateFlow<UiState<Album>>(UiState.Loading)
    val uiState: StateFlow<UiState<Album>> = _uiState.asStateFlow()

    init {
        loadAlbum()
    }

    fun loadAlbum() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(getAlbumDetail(albumId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toUiError()
            }
        }
    }

    companion object {
        const val ALBUM_ID_KEY = "albumId"
    }
}
