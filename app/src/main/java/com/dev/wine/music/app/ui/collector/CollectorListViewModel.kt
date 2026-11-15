package com.dev.wine.music.app.ui.collector

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.coroutines.DefaultDispatcher
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.usecase.GetCollectorsUseCase
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.common.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * What the collector list (HU05) shows. [collectors] is already filtered by [query];
 * [totalCount] tells an empty backend apart from a search without matches.
 */
@Immutable
data class CollectorListUiState(
    val collectors: UiState<List<Collector>> = UiState.Loading,
    val totalCount: Int = 0,
    val query: String = "",
)

@HiltViewModel
class CollectorListViewModel @Inject constructor(
    private val getCollectors: GetCollectorsUseCase,
    private val savedStateHandle: SavedStateHandle,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val loadedCollectors = MutableStateFlow<UiState<List<Collector>>>(UiState.Loading)

    // The query lives in SavedStateHandle, so it survives rotation and process death.
    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")

    // The search filters the list already in memory: typing never calls the API.
    val uiState: StateFlow<CollectorListUiState> =
        combine(loadedCollectors, query) { state, query ->
            when (state) {
                is UiState.Success -> CollectorListUiState(
                    collectors = UiState.Success(state.data.filterBy(query)),
                    totalCount = state.data.size,
                    query = query,
                )
                else -> CollectorListUiState(collectors = state, query = query)
            }
        }
            // Filtering normalizes every name and email: keep it off the main thread.
            .flowOn(defaultDispatcher)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CollectorListUiState())

    init {
        loadCollectors()
    }

    fun loadCollectors() {
        viewModelScope.launch {
            loadedCollectors.value = UiState.Loading
            loadedCollectors.value = try {
                UiState.Success(getCollectors())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.toUiError()
            }
        }
    }

    fun onQueryChange(value: String) {
        savedStateHandle[KEY_QUERY] = value
    }

    private companion object {
        const val KEY_QUERY = "query"
    }
}
