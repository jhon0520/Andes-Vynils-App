package com.dev.wine.music.app.ui.onboarding

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.wine.music.app.domain.model.Collector
import com.dev.wine.music.app.domain.usecase.GetCollectorsUseCase
import com.dev.wine.music.app.domain.usecase.SelectCollectorUseCase
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.ui.common.toUiError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer
import javax.inject.Inject

/**
 * What "¿Quién eres?" shows. [collectors] is already filtered by [query]; [totalCount] tells an
 * empty backend apart from a search without matches.
 */
@Immutable
data class CollectorPickerUiState(
    val collectors: UiState<List<Collector>> = UiState.Loading,
    val totalCount: Int = 0,
    val query: String = "",
    val selected: Collector? = null,
) {
    val canContinue: Boolean get() = selected != null
}

@HiltViewModel
class CollectorPickerViewModel @Inject constructor(
    private val getCollectors: GetCollectorsUseCase,
    private val selectCollector: SelectCollectorUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val loadedCollectors = MutableStateFlow<UiState<List<Collector>>>(UiState.Loading)

    // Query and selection live in SavedStateHandle, so they survive rotation.
    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")
    private val selectedId = savedStateHandle.getStateFlow<Int?>(KEY_SELECTED_ID, null)

    val uiState: StateFlow<CollectorPickerUiState> =
        combine(loadedCollectors, query, selectedId) { state, query, selectedId ->
            when (state) {
                is UiState.Success -> CollectorPickerUiState(
                    collectors = UiState.Success(state.data.filterBy(query)),
                    totalCount = state.data.size,
                    query = query,
                    selected = state.data.firstOrNull { it.id == selectedId },
                )
                else -> CollectorPickerUiState(collectors = state, query = query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CollectorPickerUiState())

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
        // A search that hides the selected collector also clears the selection,
        // so "Continuar como X" never points to someone who is not on screen.
        val collectors = (loadedCollectors.value as? UiState.Success)?.data ?: return
        val selected = collectors.firstOrNull { it.id == selectedId.value } ?: return
        if (listOf(selected).filterBy(value).isEmpty()) {
            savedStateHandle[KEY_SELECTED_ID] = null
        }
    }

    fun onCollectorSelect(collectorId: Int) {
        savedStateHandle[KEY_SELECTED_ID] = collectorId
    }

    /** Starts the collector session. Returns false when nobody is selected yet. */
    fun onContinue(): Boolean {
        val collector = uiState.value.selected ?: return false
        selectCollector(collector)
        return true
    }

    private companion object {
        const val KEY_QUERY = "query"
        const val KEY_SELECTED_ID = "selectedId"
    }
}

private val COMBINING_MARKS = Regex("\\p{Mn}+")

/** Lowercase without accents, so "bellon" finds "Bellón". */
internal fun String.normalizedForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace(COMBINING_MARKS, "").lowercase().trim()

internal fun List<Collector>.filterBy(query: String): List<Collector> {
    val needle = query.normalizedForSearch()
    if (needle.isEmpty()) return this
    return filter { it.name.normalizedForSearch().contains(needle) || it.email.normalizedForSearch().contains(needle) }
}

/** "Manolo Bellón" → "MB". A collector without a name shows "··". */
internal fun collectorInitials(name: String): String {
    val initials = name.trim().split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
    return initials.ifEmpty { "··" }
}
