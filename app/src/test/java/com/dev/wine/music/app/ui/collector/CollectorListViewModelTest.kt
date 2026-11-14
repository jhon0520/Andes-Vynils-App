package com.dev.wine.music.app.ui.collector

import com.dev.wine.music.app.domain.model.AppError
import com.dev.wine.music.app.domain.usecase.GetCollectorsUseCase
import com.dev.wine.music.app.fakes.FakeCollectorRepository
import com.dev.wine.music.app.fakes.TestData
import com.dev.wine.music.app.fakes.testDispatcher
import com.dev.wine.music.app.ui.common.ErrorType
import com.dev.wine.music.app.ui.common.UiState
import com.dev.wine.music.app.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CollectorListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeCollectorRepository(
        collectors = listOf(
            TestData.collector(id = 100, name = "Manolo Bellón"),
            TestData.collector(id = 101, name = "Jaime Monsalve"),
        ),
    )

    private fun createViewModel() = CollectorListViewModel(GetCollectorsUseCase(repository, testDispatcher))

    @Test
    fun `emits Success with the collectors sorted by name`() {
        val state = createViewModel().uiState.value as UiState.Success

        assertEquals(listOf("Jaime Monsalve", "Manolo Bellón"), state.data.map { it.name })
    }

    @Test
    fun `emits a network error and retry loads the collectors`() {
        repository.shouldFail = true
        repository.error = AppError.Network()
        val viewModel = createViewModel()
        assertEquals(ErrorType.NETWORK, (viewModel.uiState.value as UiState.Error).type)

        repository.shouldFail = false
        viewModel.loadCollectors()

        assertTrue(viewModel.uiState.value is UiState.Success)
    }
}
