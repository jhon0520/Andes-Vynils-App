package com.dev.wine.music.app.ui.collector

import androidx.lifecycle.SavedStateHandle
import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.usecase.GetCollectorsUseCase
import com.dev.wine.music.app.fakes.FakeCollectorRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.fakes.testDispatcher
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CollectorListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCollectorRepository(
        collectors = listOf(
            TestData.collector(id = 100, name = "Manolo Bellón", email = "manollo@caracol.com.co"),
            TestData.collector(id = 101, name = "Jaime Monsalve", email = "jmonsalve@rtvc.com.co"),
        ),
    )

    /** uiState uses stateIn(WhileSubscribed): it only computes while someone collects it. */
    private fun TestScope.createViewModel(savedState: SavedStateHandle = SavedStateHandle()) =
        CollectorListViewModel(GetCollectorsUseCase(repository, testDispatcher), savedState, testDispatcher)
            .also { viewModel ->
                backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
            }

    private fun CollectorListViewModel.shownNames(): List<String> =
        (uiState.value.collectors as UiState.Success).data.map { it.name }

    @Test
    fun `emits the collectors sorted by name`() = runTest {
        val viewModel = createViewModel()

        assertEquals(listOf("Jaime Monsalve", "Manolo Bellón"), viewModel.shownNames())
        assertEquals(2, viewModel.uiState.value.totalCount)
        assertEquals("", viewModel.uiState.value.query)
    }

    @Test
    fun `filters by name ignoring case and accents`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("BELLON")

        assertEquals(listOf("Manolo Bellón"), viewModel.shownNames())
        assertEquals("BELLON", viewModel.uiState.value.query)
    }

    @Test
    fun `filters by email`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("rtvc.com")

        assertEquals(listOf("Jaime Monsalve"), viewModel.shownNames())
    }

    @Test
    fun `a search without matches is an empty list, not an empty backend`() = runTest {
        val viewModel = createViewModel()

        viewModel.onQueryChange("zzz")

        assertEquals(emptyList<String>(), viewModel.shownNames())
        assertEquals(2, viewModel.uiState.value.totalCount)
    }

    @Test
    fun `clearing the search shows everyone again`() = runTest {
        val viewModel = createViewModel()
        viewModel.onQueryChange("monsalve")

        viewModel.onQueryChange("")

        assertEquals(listOf("Jaime Monsalve", "Manolo Bellón"), viewModel.shownNames())
    }

    @Test
    fun `searching filters in memory without calling the repository again`() = runTest {
        val viewModel = createViewModel()
        val callsAfterLoad = repository.calls

        viewModel.onQueryChange("jaime")
        viewModel.onQueryChange("manolo")

        assertEquals(1, callsAfterLoad)
        assertEquals(callsAfterLoad, repository.calls)
    }

    @Test
    fun `the query is restored from SavedStateHandle`() = runTest {
        val viewModel = createViewModel(SavedStateHandle(mapOf("query" to "monsalve")))

        assertEquals("monsalve", viewModel.uiState.value.query)
        assertEquals(listOf("Jaime Monsalve"), viewModel.shownNames())
    }

    @Test
    fun `emits a network error and retry loads the collectors`() = runTest {
        repository.shouldFail = true
        repository.error = AppError.Network()
        val viewModel = createViewModel()
        assertEquals(ErrorType.NETWORK, (viewModel.uiState.value.collectors as UiState.Error).type)

        repository.shouldFail = false
        viewModel.loadCollectors()

        assertTrue(viewModel.uiState.value.collectors is UiState.Success)
    }

    @Test
    fun `an empty backend reports zero collectors`() = runTest {
        repository.collectors = emptyList()

        val viewModel = createViewModel()

        assertEquals(0, viewModel.uiState.value.totalCount)
        assertEquals(emptyList<String>(), viewModel.shownNames())
    }
}
