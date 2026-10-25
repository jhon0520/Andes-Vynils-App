package com.dev.wine.music.app.ui.collector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.usecase.GetCollectorsUseCase
import com.dev.wine.music.app.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectorListViewModel @Inject constructor(
    private val getCollectors: GetCollectorsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Collector>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Collector>>> = _uiState.asStateFlow()

    init {
        loadCollectors()
    }

    fun loadCollectors() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(getCollectors())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                UiState.Error(e.message)
            }
        }
    }
}
