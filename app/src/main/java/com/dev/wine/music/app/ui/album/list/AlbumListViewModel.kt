package com.dev.wine.music.app.ui.album.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.Album
import com.dev.wine.music.app.domain.usecase.GetAlbumsUseCase
import com.dev.wine.music.app.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val getAlbums: GetAlbumsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Album>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Album>>> = _uiState.asStateFlow()

    init {
        loadAlbums()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(getAlbums())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.message)
            }
        }
    }
}
