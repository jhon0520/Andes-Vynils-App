package com.dev.wine.music.app.ui.collector.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.CollectorDetail
import com.dev.wine.music.app.domain.usecase.GetCollectorDetailUseCase
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
class CollectorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCollectorDetail: GetCollectorDetailUseCase,
) : ViewModel() {

    // Navigation puts the CollectorDetailRoute.collectorId argument here under its property name.
    private val collectorId: Int = checkNotNull(savedStateHandle[COLLECTOR_ID_KEY])

    private val _uiState = MutableStateFlow<UiState<CollectorDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<CollectorDetail>> = _uiState.asStateFlow()

    init {
        loadCollector()
    }

    fun loadCollector() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = try {
                UiState.Success(getCollectorDetail(collectorId))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toUiError()
            }
        }
    }

    companion object {
        const val COLLECTOR_ID_KEY = "collectorId"
    }
}
